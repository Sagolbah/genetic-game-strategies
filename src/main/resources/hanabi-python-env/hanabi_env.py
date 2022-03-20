# Modified runner from Hanabi Learning Environment example

import sys
import getopt
import json
import asyncio
import websockets
import random
from hanabi_learning_environment import rl_env
from hanabi_actions import action

from hanabi_learning_environment.rl_env import Agent


def parse_action(name):
    return name.split('.')[-1]


class MyAgent(Agent):
    """Agent that applies a simple heuristic."""

    def __init__(self, config, strategy, *args, **kwargs):
        """Initialize the agent."""
        self.config = config
        self.strategy = json.loads(strategy)
        # Extract max info tokens or set default to 8.
        self.max_information_tokens = config.get('information_tokens', 8)

    @staticmethod
    def playable_card(card, fireworks):
        """A card is playable if it can be placed on the fireworks pile."""
        return card['rank'] == fireworks[card['color']]

    def act(self, observation):
        """Act based on an observation."""
        if observation['current_player_offset'] != 0:
            return None

        for rule in self.strategy:
            result = action(observation, parse_action(rule['type']))
            if result is not None:
                return result

        # Placeholder discard
        return {'action_type': 'DISCARD', 'card_index': 0}


class Runner(object):
    """Runner class."""

    def __init__(self, flags, strategy):
        """Initialize runner."""
        self.flags = flags
        self.agent_config = {'players': flags['players']}
        self.environment = rl_env.make('Hanabi-Full', num_players=flags['players'])
        self.strategy = strategy
        self.agent_class = MyAgent

    def run(self):
        """Run episodes."""
        rewards = []
        for episode in range(self.flags['num_episodes']):
            observations = self.environment.reset()
            agents = [self.agent_class(self.agent_config, self.strategy)
                      for _ in range(self.flags['players'])]
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
                print('Agent: {} action: {}'.format(observation['current_player'],
                                                    current_player_action))
                observations, reward, done, _ = self.environment.step(
                    current_player_action)
                episode_reward += reward
            rewards.append(episode_reward)
            print('Running episode: %d' % episode)
            print('Max Reward: %.3f' % max(rewards))
        return rewards


async def fit(websocket):
    message = await websocket.recv()
    print(message)
    flags = {'players': 2, 'num_episodes': 1}
    runner = Runner(flags, message)
    score = runner.run()
    await websocket.send(str(score[0]))
    print(f">>> {score}")


async def main():
    async with websockets.serve(fit, "localhost", 8765):
        await asyncio.Future()  # run forever


if __name__ == "__main__":
    asyncio.run(main())
