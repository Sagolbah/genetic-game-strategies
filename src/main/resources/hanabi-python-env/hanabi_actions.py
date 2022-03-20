import random


# All actions return None if they are impossible / not available


def action(observation, name):
    return action_map[name](observation)


def safe_play(observation):
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['color'] is not None and hint['rank'] is not None and observation['fireworks'][hint['color']] == \
                hint['rank']:
            return {'action_type': 'PLAY', 'card_index': card_index}
    return None


def random_hint(observation):
    if observation['information_tokens'] > 0:
        moves = list(filter(lambda x: x['action_type'].startswith('REVEAL'), observation['legal_moves']))
        return random.choice(moves)
    return None


def random_discard(observation):
    return {'action_type': 'DISCARD', 'card_index': random.randint(0, len(observation['card_knowledge'][0]) - 1)}


action_map = {
    "SafePlay": safe_play,
    "RandomHint": random_hint,
    "RandomDiscard": random_discard
}


