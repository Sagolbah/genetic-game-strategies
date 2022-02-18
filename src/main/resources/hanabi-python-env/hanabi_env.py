# Modified runner from Hanabi Learning Environment example

import sys
import getopt
import asyncio
import websockets
from hanabi_learning_environment import rl_env
from hanabi_learning_environment.agents.random_agent import RandomAgent


class Runner(object):
  """Runner class."""

  def __init__(self, flags):
    """Initialize runner."""
    self.flags = flags
    self.agent_config = {'players': flags['players']}
    self.environment = rl_env.make('Hanabi-Full', num_players=flags['players'])
    self.agent_class = RandomAgent

  def run(self):
    """Run episodes."""
    rewards = []
    for episode in range(self.flags['num_episodes']):
      observations = self.environment.reset()
      agents = [self.agent_class(self.agent_config)
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
    flags = {'players': 2, 'num_episodes': 1}
    runner = Runner(flags)
    score = runner.run()
    await websocket.send(str(score))
    print(f">>> {score}")

async def main():
    async with websockets.serve(fit, "localhost", 8765):
        await asyncio.Future()  # run forever

if __name__ == "__main__":
    asyncio.run(main())
