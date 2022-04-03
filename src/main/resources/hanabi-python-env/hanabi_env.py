# Modified runner from Hanabi Learning Environment example

import json
import asyncio
import random

import websockets
from statistics import mean
from hanabi_learning_environment import rl_env, pyhanabi
from hanabi_actions import action, parse_action, terminal_safe_legal_random

from hanabi_learning_environment.rl_env import Agent


class HanabiAgent(Agent):

    def __init__(self, config, strategy):
        """Initialize the agent."""
        self.config = config
        self.strategy = strategy
        self.max_information_tokens = config.get('information_tokens', 8)
        # Utility data for testing agents. Evolved agents must not use this data.
        self.time = 0
        self.card_time = [0] * 5

    def act(self, observation):
        if observation['current_player_offset'] != 0:
            return None
        result = self.do_act(observation)
        if result['action_type'] == 'PLAY' or result['action_type'] == 'DISCARD':
            self.time += 1
            self.card_time[result['card_index']] = self.time
        return result

    def do_act(self, observation):
        """Act based on an observation."""
        for rule in self.strategy:
            result = action(observation, rule, self.card_time)
            if result is not None:
                # print('Agent: {}, Action type: {}, Final action: {}'.format(observation['current_player'], parse_action(rule['type']), result))
                return result
        # Legal random action if all rules were not applicable
        result = terminal_safe_legal_random(observation)
        # print('Agent: {}, Action type: Terminal legal random, Final action: {}'.format(observation['current_player'], result))
        return result


class Runner(object):
    """Runner class."""

    def __init__(self, config):
        """Initialize runner."""
        self.config = config
        self.agent_config = {'players': len(config['players'])}
        random.seed(config['seed'])
        self.seeds = [random.randint(0, 100000000) for _ in range(config['rounds'])]
        self.agent_class = HanabiAgent

    def run(self):
        """Run episodes."""
        rewards = []
        for episode in range(self.config['rounds']):
            random.seed(self.seeds[episode])
            environment = self.make_environment(self.seeds[episode], len(self.config['players']))
            observations = environment.reset()
            agents = [self.agent_class(self.agent_config, strategy)
                      for strategy in self.config['players']]
            done = False
            episode_reward = 0
            while not done:
                for agent_id, agent in enumerate(agents):
                    observation = observations['player_observations'][agent_id]
                    action = agent.act(observation)
                    if observation['current_player'] == agent_id:
                        assert action is not None
                        current_player_action = action
                    else:
                        assert action is None
                # Make an environment step.
                observations, reward, done, _ = environment.step(
                    current_player_action)
                episode_reward += reward
            rewards.append(episode_reward)
            print('Running episode: %d' % episode)
            print('Last Reward: %d' % episode_reward)
        return rewards

    @staticmethod
    def make_environment(seed, players):
        return rl_env.HanabiEnv(config={
            "colors": 5,
            "ranks": 5,
            "players": players,
            "max_information_tokens": 8,
            "max_life_tokens": 3,
            "seed": seed,
            "observation_type": pyhanabi.AgentObservationType.CARD_KNOWLEDGE.value})


async def fit(websocket):
    message = await websocket.recv()
    print(message)
    runner = Runner(json.loads(message))
    rewards = runner.run()
    fitness = mean(rewards)
    await websocket.send(str(fitness))
    print(f">>> {rewards}")
    print("Average: " + str(fitness))


async def main():
    async with websockets.serve(fit, "localhost", 8765):
        await asyncio.Future()  # run forever


if __name__ == "__main__":
    asyncio.run(main())
