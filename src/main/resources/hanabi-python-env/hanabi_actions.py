import random


# All actions return None if they are impossible / not available

def parse_action(name):
    return name.split('.')[-1]


def action(observation, rule):
    name = parse_action(rule['type'])
    if name in probability_action_map:
        return probability_action_map[name](observation, float(rule['probability']))
    return action_map[name](observation)


def safe_play(observation):
    # Check equal ranks of piles
    firework_ranks = set(observation['fireworks'].values())
    safe_rank = -1
    if len(firework_ranks) == 1:
        safe_rank = next(iter(firework_ranks))
    safe_cards_indices = set()
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['rank'] == safe_rank:
            safe_cards_indices.add(card_index)
            continue
        if hint['color'] is not None and hint['rank'] is not None and playable_card(hint, observation['fireworks']):
            safe_cards_indices.add(card_index)
    if safe_cards_indices:
        return {'action_type': 'PLAY', 'card_index': random.choice(list(safe_cards_indices))}
    return None


def probability_play(observation, probability):
    if observation['life_tokens'] == 1:  # do not take risk if we can't lose life tokens
        return None
    safe_attempt = safe_play(observation)
    if safe_attempt is not None:  # corner case -- a safe card with probability 1
        return safe_attempt
    best_prob = -1
    best_idx = -1
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['color'] is not None and hint['rank'] is None:
            prob = get_probability_for_color(observation, hint['color'])
            if prob > best_prob:
                best_idx = card_index
                best_prob = prob
        elif hint['color'] is None and hint['rank'] is not None:
            prob = get_probability_for_color(observation, hint['rank'])
            if prob > best_prob:
                best_idx = card_index
                best_prob = prob
    if best_prob < probability or best_idx == -1:
        return None
    return {'action_type': 'PLAY', 'card_index': best_idx}


# NOTE: affects next player
def playable_hint(observation):
    if observation['information_tokens'] == 0:
        return None
    given_hints = observation['card_knowledge'][1]
    playable_cards = get_all_playable_cards(observation)
    # try to finish hint
    playable_hints = get_completing_hints(observation)
    # use random hint about useful card
    for idx, card in playable_cards:
        if given_hints[idx]['rank'] is None and given_hints[idx]['color'] is None:
            if bool(random.getrandbits(1)):
                playable_hints.append({'action_type': 'REVEAL_RANK', 'target_offset': 1, 'rank': card['rank']})
            else:
                playable_hints.append({'action_type': 'REVEAL_COLOR', 'target_offset': 1, 'color': card['color']})
    if playable_hints:
        return random.choice(playable_hints)
    return None


def complete_playable_hint(observation):
    if observation['information_tokens'] == 0:
        return None
    completing_hints = get_completing_hints(observation)
    if completing_hints:
        return random.choice(completing_hints)
    return None


def weak_playable_hint(observation):
    if observation['information_tokens'] == 0:
        return None
    playable_cards = get_all_playable_cards(observation)
    hints = set()
    for _, card in playable_cards:
        hints.add({'action_type': 'REVEAL_RANK', 'target_offset': 1, 'rank': card['rank']})
        hints.add({'action_type': 'REVEAL_COLOR', 'target_offset': 1, 'color': card['color']})
    return random.choice(list(hints))


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


def useless_discard(observation):
    if observation['information_tokens'] == 8:
        return None
    for card_index, card in enumerate(observation['card_knowledge'][0]):
        if is_useless(observation['fireworks'], observation['discard_pile'], card):
            return {'action_type': 'DISCARD', 'card_index': card_index}
    return None


# TODO: Check discard for cards with 2 hints
def is_useless(fireworks, discard_pile, card):
    if card['color'] is None and card['rank'] is None:
        return False
    # Check color
    if card['color'] is not None and fireworks[card['color']] == 5:
        return True
    # Check rank
    if card['rank'] is not None and card['rank'] > min(fireworks.values()):
        return True
    return False


def playable_card(card, fireworks):
    """A card is playable if it can be placed on the fireworks pile."""
    return card['rank'] is not None and card['color'] is not None and card['rank'] == fireworks[card['color']]


def get_completing_hints(observation):
    given_hints = observation['card_knowledge'][1]
    playable_cards = get_all_playable_cards(observation)
    answer = []
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
            answer.append(mp)
    return answer


# Returns all playable cards for next player
def get_all_playable_cards(observation):
    fireworks = observation['fireworks']
    return [(idx, card) for idx, card in enumerate(observation['observed_hands'][1]) if playable_card(card, fireworks)]


# Legal random "discard/hint" action. Used only when all rules are not applicable for current observation.
def terminal_safe_legal_random(observation):
    moves = list(filter(lambda x: x['action_type'] != 'PLAY', observation['legal_moves']))
    return random.choice(moves)


def legal_random(observation):
    return random.choice(observation['legal_moves'])


# If we had a "good" card with full information, it would be played in safe_play corner case.
def get_probability_for_color(observation, color):
    required_rank = observation['fireworks'][color]
    possible_cards = 10  # possible cards that could fit into given slot (i.e. which are green)
    playable_cards = cards_per_rank[required_rank]
    for card in observation['discard_pile']:  # discard
        if card['color'] == color:
            possible_cards -= 1
            if card['rank'] == required_rank:
                playable_cards -= 1
    for hand in observation['observed_hands']:  # across players, including myself
        for card in hand:
            if card['color'] == color:
                possible_cards -= 1
                if card['rank'] == required_rank:
                    playable_cards -= 1
    return 0 if possible_cards == 0 else playable_cards / possible_cards


def get_probability_for_rank(observation, rank):
    possible_cards = cards_per_rank[rank] * 5  # 5 colors
    playable_cards = cards_per_rank[rank] * sum(1 for x in observation['fireworks'].values() if x == rank)
    for card in observation['discard_pile']:  # discard
        if card['rank'] == rank:
            possible_cards -= 1
            if playable_card(observation['fireworks'], card):
                playable_cards -= 1
    for hand in observation['observed_hands']:  # across players, including myself
        for card in hand:
            if card['rank'] == rank:
                possible_cards -= 1
                if playable_card(card, observation['fireworks']):
                    playable_cards -= 1
    return 0 if possible_cards == 0 else playable_cards / possible_cards


action_map = {
    "SafePlay": safe_play,
    "PlayableHint": playable_hint,
    "CompletePlayableHint": complete_playable_hint,
    "RandomHint": random_hint,
    "NonHintedDiscard": non_hinted_discard,
    "RandomDiscard": random_discard,
    "UselessDiscard": useless_discard,
    "LegalRandom": legal_random,
    "WeakPlayableHint": weak_playable_hint
}

probability_action_map = {
    "ProbabilityPlay": probability_play
}

cards_per_rank = {
    0: 3,
    1: 2,
    2: 2,
    3: 2,
    4: 1,
    5: 0
}