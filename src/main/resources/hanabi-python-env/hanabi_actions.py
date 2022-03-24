import random


# All actions return None if they are impossible / not available


def action(observation, name):
    return action_map[name](observation)


def safe_play(observation):
    # Check equal ranks of piles
    firework_ranks = set(observation['fireworks'].values())
    safe_rank = -1
    if len(firework_ranks) == 1:
        safe_rank = next(iter(firework_ranks))
    safe_cards_indices = []
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['rank'] == safe_rank:
            safe_cards_indices.append(card_index)
            continue
        if hint['color'] is not None and hint['rank'] is not None and observation['fireworks'][hint['color']] == \
                hint['rank']:
            safe_cards_indices.append(card_index)
    if safe_cards_indices:
        return {'action_type': 'PLAY', 'card_index': random.choice(safe_cards_indices)}
    return None


# NOTE: affects next player
def playable_hint(observation):
    if observation['information_tokens'] == 0:
        return None
    given_hints = observation['card_knowledge'][1]
    fireworks = observation['fireworks']
    playable_cards = [(idx, card) for idx, card in enumerate(observation['observed_hands'][1])
                      if playable_card(card, fireworks)]
    random.shuffle(playable_cards)
    # try to finish hint
    for idx, card in playable_cards:
        hinted_rank = given_hints[idx]['rank'] is not None
        hinted_color = given_hints[idx]['color'] is not None
        if hinted_color ^ hinted_rank:  # do not hint 2 hints done, hint remaining
            action_type = 'REVEAL_' + ('COLOR' if hinted_rank else 'RANK')
            mp = {'action_type': action_type, 'target_offset': 1}
            if hinted_rank:
                mp['color'] = card['color']
            else:
                mp['rank'] = card['rank']
            return mp
    # use random hint about useful card
    for idx, card in playable_cards:
        if given_hints[idx]['rank'] is None and given_hints[idx]['color'] is None:
            if bool(random.getrandbits(1)):
                return {'action_type': 'REVEAL_RANK', 'target_offset': 1, 'rank': card['rank']}
            else:
                return {'action_type': 'REVEAL_COLOR', 'target_offset': 1, 'color': card['color']}
    return None


def random_hint(observation):
    if observation['information_tokens'] > 0:
        moves = list(filter(lambda x: x['action_type'].startswith('REVEAL'), observation['legal_moves']))
        return random.choice(moves)
    return None


def non_hinted_discard(observation):
    if observation['information_tokens'] == 8:
        return None
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['color'] is None and hint['rank'] is None:
            return {'action_type': 'DISCARD', 'card_index': card_index}
    return None


def random_discard(observation):
    if observation['information_tokens'] == 8:
        return None
    return {'action_type': 'DISCARD', 'card_index': random.randint(0, len(observation['card_knowledge'][0]) - 1)}


def playable_card(card, fireworks):
    """A card is playable if it can be placed on the fireworks pile."""
    return card['rank'] == fireworks[card['color']]


# Legal random "discard/hint" action. Used only when all rules are not applicable for current observation.
def terminal_safe_legal_random(observation):
    moves = list(filter(lambda x: x['action_type'] != 'PLAY', observation['legal_moves']))
    return random.choice(moves)


action_map = {
    "SafePlay": safe_play,
    "PlayableHint": playable_hint,
    "RandomHint": random_hint,
    "NonHintedDiscard": non_hinted_discard,
    "RandomDiscard": random_discard
}
