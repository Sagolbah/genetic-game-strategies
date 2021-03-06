import random
from functools import partial


# All actions return None if they are impossible / not available


def action(observation, rng, rule, state):
    name = rule['type']
    if name in state_action_map:
        return state_action_map[name](observation, rng, state)
    if name in parametrized_action_map:
        return parametrized_action_map[name](observation, rng, float(rule['value']))
    return action_map[name](observation, rng)


def safe_play(observation, rng):
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
        return {'action_type': 'PLAY', 'card_index': rng.choice(list(safe_cards_indices))}
    return None


def probability_play(observation, rng, probability, default_best=-1, default_idx=-1):
    if observation['life_tokens'] == 1:  # do not take risk if we can't lose life tokens
        return None
    safe_attempt = safe_play(observation, rng)
    if safe_attempt is not None:  # corner case -- a safe card with probability 1
        return safe_attempt
    best_prob = default_best
    best_idx = default_idx
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['color'] is not None and hint['rank'] is None:
            prob = get_probability_for_color(observation, hint['color'])
            if prob > best_prob:
                best_idx = card_index
                best_prob = prob
        elif hint['color'] is None and hint['rank'] is not None:
            prob = get_probability_for_rank(observation, hint['rank'])
            if prob > best_prob:
                best_idx = card_index
                best_prob = prob
    if best_prob < probability or best_idx == -1:
        return None
    return {'action_type': 'PLAY', 'card_index': best_idx}


def empty_deck_probability_play(observation, rng, probability):
    if observation['deck_size'] != 0 or observation['life_tokens'] == 1:
        return None
    return probability_play(observation, rng, probability)


def full_probability_play(observation, rng, probability):
    if observation['life_tokens'] == 1:  # do not take risk if we can't lose life tokens
        return None
    hint_idx = -1
    for idx, card in enumerate(observation['card_knowledge'][0]):
        if card['rank'] is None and card['color'] is None:
            hint_idx = idx
    if hint_idx == -1:
        return probability_play(observation, rng, probability)
    cards_left = {}
    for color in game_colors:
        cards_left[color] = cards_per_rank.copy()
    for card in observation['discard_pile']:
        cards_left[card['color']][card['rank']] -= 1
    for i in range(1, len(observation['observed_hands'])):
        for card in observation['observed_hands'][i]:
            cards_left[card['color']][card['rank']] -= 1
    for card in observation['card_knowledge'][0]:
        if card['rank'] is not None and card['color'] is not None:
            cards_left[card['color']][card['rank']] -= 1
    for color in game_colors:
        for i in range(observation['fireworks'][color]):
            cards_left[color][i] -= 1
    possible_cards = 0
    playable_cards = 0
    for color in game_colors:
        for rank in range(5):
            possible_cards += cards_left[color][rank]
            if observation['fireworks'][color] == rank:
                playable_cards += cards_left[color][rank]
    assert possible_cards >= 0 and playable_cards >= 0
    prob = 0 if possible_cards == 0 else playable_cards / possible_cards
    return probability_play(observation, rng, probability, default_best=prob, default_idx=hint_idx)


def full_empty_deck_probability_play(observation, rng, probability):
    if observation['deck_size'] != 0 or observation['life_tokens'] == 1:
        return None
    return full_probability_play(observation, rng, probability)


# noinspection PyTypeChecker
def playable_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    playable_hints = []
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        playable_cards = get_all_playable_cards(observation, i)
        for idx, card in playable_cards:
            playable_hints += get_missing_hints(given_hints, idx, card, i)
    if playable_hints:
        return rng.choice(playable_hints)
    return None


def complete_playable_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    completing_hints = []
    for i in range(1, len(observation['observed_hands'])):
        completing_hints += get_completing_hints(observation, i)
    if completing_hints:
        return rng.choice(completing_hints)
    return None


def weak_playable_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    playable_cards = get_all_playable_cards(observation, 1)
    hints = []
    for _, card in playable_cards:  # TODO: do we need set here?
        hints.append({'action_type': 'REVEAL_RANK', 'target_offset': 1, 'rank': card['rank']})
        hints.append({'action_type': 'REVEAL_COLOR', 'target_offset': 1, 'color': card['color']})
    return rng.choice(hints) if hints else None


def walton_playable_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        playable_cards = get_all_playable_cards(observation, i)
        for idx, card in playable_cards:
            if given_hints[idx]['rank'] is None:
                return {'action_type': 'REVEAL_RANK', 'target_offset': i, 'rank': card['rank']}
            elif given_hints[idx]['color'] is None:
                return {'action_type': 'REVEAL_COLOR', 'target_offset': i, 'color': card['color']}
    return None


def random_hint(observation, rng):
    if observation['information_tokens'] > 0:
        moves = list(filter(lambda x: x['action_type'].startswith('REVEAL'), observation['legal_moves']))
        return rng.choice(moves)
    return None


def useless_card_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    hints = []
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        for idx, card in enumerate(observation['observed_hands'][i]):
            if not is_useless(observation['fireworks'], observation['discard_pile'], card):
                continue
            hints += get_missing_hints(given_hints, idx, card, i)
    return rng.choice(hints) if hints else None


def rank_hint(observation, rng, rank):
    if observation['information_tokens'] == 0:
        return None
    rank = int(rank)
    for i in range(1, len(observation['observed_hands'])):
        move = {'action_type': 'REVEAL_RANK', 'target_offset': i, 'rank': rank}
        return move if any(map(lambda x: x['rank'] == rank, observation['observed_hands'][1])) else None
    return None


def piers_useless_card_hint(observation, rng):
    if observation['information_tokens'] >= 4 or observation['information_tokens'] == 0:
        return None
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        for idx, card in enumerate(observation['observed_hands'][i]):
            if given_hints[idx]['color'] is None:
                if observation['fireworks'][card['color']] == 5:
                    return {'action_type': 'REVEAL_COLOR', 'target_offset': i, 'color': card['color']}
            if given_hints[idx]['rank'] is None:
                if card['rank'] < min(observation['fireworks'].values()):
                    return {'action_type': 'REVEAL_RANK', 'target_offset': i, 'rank': card['rank']}
            if (given_hints[idx]['rank'] is None) ^ (given_hints[idx]['color'] is None):
                if card['rank'] < observation['fireworks'][card['color']]:
                    hints = get_missing_hints(given_hints, idx, card, i)
                    return hints[0]
    return None


def unknown_outer_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        for idx, card in enumerate(observation['observed_hands'][i]):
            hints = get_missing_hints(given_hints, idx, card, i)
            return rng.choice(hints) if hints else None
    return None


def greedy_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    best_hint = None
    best_info = 0
    moves = list(filter(lambda x: x['action_type'].startswith('REVEAL'), observation['legal_moves']))
    for move in moves:
        info_cnt = 0
        given_hints = observation['card_knowledge'][move['target_offset']]
        true_hand = observation['observed_hands'][move['target_offset']]
        if move['action_type'] == 'REVEAL_COLOR':
            for i in range(len(given_hints)):
                if given_hints[i]['color'] is None and true_hand[i]['color'] == move['color']:
                    info_cnt += 1
        else:
            for i in range(len(given_hints)):
                if given_hints[i]['rank'] is None and true_hand[i]['rank'] == move['rank']:
                    info_cnt += 1
        if info_cnt > best_info:
            best_info = info_cnt
            best_hint = move
    return best_hint


def unknown_card_hint(observation, rng):
    if observation['information_tokens'] == 0:
        return None
    moves = list(filter(lambda x: x['action_type'].startswith('REVEAL'), observation['legal_moves']))
    hints = []
    for move in moves:
        given_hints = observation['card_knowledge'][move['target_offset']]
        for i in range(len(given_hints)):
            if given_hints[i]['color'] is None and given_hints[i]['rank'] is None:
                hints.append(move)
    return rng.choice(hints) if hints else None


def stack_defense_hint(observation, rng, max_rank):
    if observation['information_tokens'] == 0:
        return None
    discard_pile = observation['discard_pile']
    required_cards = dict(zip(game_colors, [0] * 5))
    hints = []
    max_rank = int(max_rank)
    for card in discard_pile:
        if playable_card(card, observation['fireworks']):
            required_cards[card['color']] += 1
    colors_to_defend = set()
    for color in game_colors:
        pile_rank = observation['fireworks'][color]
        if required_cards[color] == cards_per_rank[pile_rank] - 1 and pile_rank <= max_rank:
            colors_to_defend.add((color, pile_rank))
    if len(colors_to_defend) == 0:
        return None  # do not iterate hands
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        for idx, card in enumerate(observation['observed_hands'][i]):
            if (card['color'], card['rank']) in colors_to_defend:
                hints += get_missing_hints(given_hints, idx, card, i)
    return rng.choice(hints) if hints else None


def future_stack_defense_hint(observation, rng, max_rank):
    if observation['information_tokens'] == 0:
        return None
    discard_pile = observation['discard_pile']
    fireworks = observation['fireworks']
    hints = []
    most_possible = dict(zip(game_colors, map(lambda x: find_most_possible_rank(x, discard_pile), game_colors)))
    discarded_possible = dict()
    for card in discard_pile:
        if fireworks[card['color']] <= most_possible[card['color']] and card['rank'] <= max_rank:
            card_tuple = (card['color'], card['rank'])
            discarded_possible[card_tuple] = discarded_possible.get(card_tuple, 0) + 1
    colors_to_defend = {}
    for rank in range(5):
        for color in game_colors:
            if fireworks[color] > rank:  # already placed - no need to call
                pass
            if colors_to_defend.get(color) is not None:  # use min rank for hint
                pass
            if discarded_possible.get((color, rank), -1) == cards_per_rank[rank] - 1:
                colors_to_defend[color] = rank
    if len(colors_to_defend) == 0:
        return None  # do not iterate hands
    for i in range(1, len(observation['observed_hands'])):
        given_hints = observation['card_knowledge'][i]
        for idx, card in enumerate(observation['observed_hands'][i]):
            if (card['color'], card['rank']) in colors_to_defend:
                hints += get_missing_hints(given_hints, idx, card, i)
    return rng.choice(hints) if hints else None


def non_hinted_discard(observation, rng):
    if observation['information_tokens'] == 8:
        return None
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['color'] is None and hint['rank'] is None:
            return {'action_type': 'DISCARD', 'card_index': card_index}
    return None


def random_discard(observation, rng):
    if observation['information_tokens'] == 8:
        return None
    return {'action_type': 'DISCARD', 'card_index': rng.randint(0, len(observation['card_knowledge'][0]) - 1)}


def useless_discard(observation, rng):
    if observation['information_tokens'] == 8:
        return None
    for card_index, card in enumerate(observation['card_knowledge'][0]):
        if is_useless(observation['fireworks'], observation['discard_pile'], card):
            return {'action_type': 'DISCARD', 'card_index': card_index}
    return None


def oldest_discard(observation, rng, state):
    if observation['information_tokens'] == 8:
        return None
    oldest_idx = min(range(len(observation['card_knowledge'][0])), key=state.__getitem__)
    return {'action_type': 'DISCARD', 'card_index': oldest_idx}


def vdb_probability_discard(observation, rng):
    if observation['information_tokens'] == 8:
        return None
    best_prob = -1
    best_idx = -1
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if is_useless(observation['fireworks'], observation['discard_pile'], hint):
            return {'action_type': 'DISCARD', 'card_index': card_index}
        if hint['color'] is not None and hint['rank'] is None:
            prob = vdb_useless_probability_for_color(observation, hint['color'])
            if prob > best_prob:
                best_idx = card_index
                best_prob = prob
        elif hint['color'] is None and hint['rank'] is not None:
            prob = vdb_useless_probability_for_rank(observation, hint['rank'])
            if prob > best_prob:
                best_idx = card_index
                best_prob = prob
    if best_idx == -1:
        return None
    return {'action_type': 'DISCARD', 'card_index': best_idx}


def highest_rank_discard(observation, rng):
    if observation['information_tokens'] == 8:
        return None
    best_idx = -1
    best_rank = -1
    for card_index, hint in enumerate(observation['card_knowledge'][0]):
        if hint['rank'] is not None and hint['rank'] > best_rank:
            best_rank = hint['rank']
            best_idx = card_index
    if best_idx == -1:
        return None
    return {'action_type': 'DISCARD', 'card_index': best_idx}


def is_useless(fireworks, discard_pile, card):
    if card['color'] is None and card['rank'] is None:
        return False
    # Check color
    if card['color'] is not None and fireworks[card['color']] == 5:
        return True
    # Check rank
    if card['rank'] is not None and card['rank'] < min(fireworks.values()):
        return True
    # Rules for fully known cards.
    if card['rank'] is not None and card['color'] is not None:
        if fireworks[card['color']] > card['rank'] or card['rank'] > find_most_possible_rank(card['color'],
                                                                                             discard_pile):
            return True

    return False


def playable_card(card, fireworks):
    return card['rank'] is not None and card['color'] is not None and card['rank'] == fireworks[card['color']]


def get_completing_hints(observation, offset):
    given_hints = observation['card_knowledge'][offset]
    playable_cards = get_all_playable_cards(observation, offset)
    answer = []
    # try to finish hint
    for idx, card in playable_cards:
        hinted_rank = given_hints[idx]['rank'] is not None
        hinted_color = given_hints[idx]['color'] is not None
        if hinted_color ^ hinted_rank:
            action_type = 'REVEAL_' + ('COLOR' if hinted_rank else 'RANK')
            mp = {'action_type': action_type, 'target_offset': offset}
            if hinted_rank:
                mp['color'] = card['color']
            else:
                mp['rank'] = card['rank']
            answer.append(mp)
    return answer


# Returns all playable cards
def get_all_playable_cards(observation, offset):
    fireworks = observation['fireworks']
    return [(idx, card) for idx, card in enumerate(observation['observed_hands'][offset]) if
            playable_card(card, fireworks)]


# Legal random "discard/hint" action. Used only when all rules are not applicable for current observation.
def terminal_safe_legal_random(observation, rng):
    moves = list(filter(lambda x: x['action_type'] != 'PLAY', observation['legal_moves']))
    return rng.choice(moves)


def legal_random(observation, rng):
    return rng.choice(observation['legal_moves'])


def find_most_possible_rank(color, discard_pile):
    discards = dict(zip(range(5), [0] * 5))
    for card in discard_pile:
        if card['color'] == color:
            discards[card['rank']] += 1
    for i in range(5):
        if discards[i] == cards_per_rank[i]:
            return i - 1
    return 4


# Invariant: There is no "good" cards with full information in our hand, it is played in safe_play corner case.
def get_probability_for_color(observation, color):
    required_rank = observation['fireworks'][color]
    possible_cards = 10  # possible cards that could fit into given slot (i.e. which are green)
    playable_cards = cards_per_rank[required_rank]
    for card in observation['discard_pile']:  # discard
        if card['color'] == color:
            possible_cards -= 1
            if card['rank'] == required_rank:
                playable_cards -= 1
    for i in range(1, len(observation['observed_hands'])):  # across players
        for card in observation['observed_hands'][i]:
            if card['color'] == color:
                possible_cards -= 1
                if card['rank'] == required_rank:
                    playable_cards -= 1
    for card in observation['card_knowledge'][0]:  # my hand
        if card['color'] == color and card['rank'] is not None:
            possible_cards -= 1
            assert not playable_card(card, observation['fireworks'])
    possible_cards -= observation['fireworks'][color]
    assert possible_cards >= 0
    return 0 if possible_cards == 0 else playable_cards / possible_cards


def get_probability_for_rank(observation, rank):
    possible_cards = cards_per_rank[rank] * 5  # 5 colors
    playable_cards = cards_per_rank[rank] * sum(1 for x in observation['fireworks'].values() if x == rank)
    for card in observation['discard_pile']:  # discard
        if card['rank'] == rank:
            possible_cards -= 1
            if playable_card(card, observation['fireworks']):
                playable_cards -= 1
    for i in range(1, len(observation['observed_hands'])):  # across players
        for card in observation['observed_hands'][i]:
            if card['rank'] == rank:
                possible_cards -= 1
                if playable_card(card, observation['fireworks']):
                    playable_cards -= 1
    for card in observation['card_knowledge'][0]:  # my hand
        if card['rank'] == rank and card['color'] is not None:
            possible_cards -= 1
            assert not playable_card(card, observation['fireworks'])
    possible_cards -= sum(1 for x in observation['fireworks'].values() if x > rank)
    assert possible_cards >= 0
    return 0 if possible_cards == 0 else playable_cards / possible_cards


def vdb_useless_probability_for_color(observation, color):
    possible_cards = cards_per_rank.copy()
    for card in observation['discard_pile']:
        if card['color'] == color:
            possible_cards[card['rank']] -= 1
    for i in range(1, len(observation['observed_hands'])):
        for card in observation['observed_hands'][i]:
            if card['color'] == color:
                possible_cards[card['rank']] -= 1
    for card in observation['card_knowledge'][0]:
        if card['color'] == color and card['rank'] is not None:
            possible_cards[card['rank']] -= 1
    for i in range(observation['fireworks'][color]):
        possible_cards[i] -= 1
    useless_cards = 0
    possible_sum = sum(possible_cards.values())
    for k, v in possible_cards.items():
        if is_useless(observation['fireworks'], observation['discard_pile'], {'color': color, 'rank': k}):
            useless_cards += v
    return 0 if possible_sum == 0 else useless_cards / possible_sum


def vdb_useless_probability_for_rank(observation, rank):
    possible_cards = dict(zip(game_colors, [cards_per_rank[rank]] * 5))
    for card in observation['discard_pile']:
        if card['rank'] == rank:
            possible_cards[card['color']] -= 1
    for i in range(1, len(observation['observed_hands'])):
        for card in observation['observed_hands'][i]:
            if card['rank'] == rank:
                possible_cards[card['color']] -= 1
    for card in observation['card_knowledge'][0]:
        if card['rank'] == rank and card['color'] is not None:
            possible_cards[card['color']] -= 1
    for color in game_colors:
        if observation['fireworks'][color] > rank:
            possible_cards[color] -= 1
    useless_cards = 0
    possible_sum = sum(possible_cards.values())
    for k, v in possible_cards.items():
        if is_useless(observation['fireworks'], observation['discard_pile'], {'color': k, 'rank': rank}):
            useless_cards += v
    return 0 if possible_sum == 0 else useless_cards / possible_sum


def get_missing_hints(given_hints, card_idx, card, offset):
    hints = []
    if given_hints[card_idx]['rank'] is None:
        hints.append({'action_type': 'REVEAL_RANK', 'target_offset': offset, 'rank': card['rank']})
    if given_hints[card_idx]['color'] is None:
        hints.append({'action_type': 'REVEAL_COLOR', 'target_offset': offset, 'color': card['color']})
    return hints


action_map = {
    "SafePlay": safe_play,
    "PlayableHint": playable_hint,
    "CompletePlayableHint": complete_playable_hint,
    "RandomHint": random_hint,
    "NonHintedDiscard": non_hinted_discard,
    "RandomDiscard": random_discard,
    "UselessDiscard": useless_discard,
    "LegalRandom": legal_random,
    "WeakPlayableHint": weak_playable_hint,
    "UselessCardHint": useless_card_hint,
    "PiersUselessCardHint": piers_useless_card_hint,
    "GreedyHint": greedy_hint,
    "UnknownCardHint": unknown_card_hint,
    "VDBProbabilityDiscard": vdb_probability_discard,
    "HighestRankDiscard": highest_rank_discard,
    "UnknownOuterHint": unknown_outer_hint,
    "WaltonPlayableHint": walton_playable_hint
}

parametrized_action_map = {
    "ProbabilityPlay": probability_play,
    "EmptyDeckProbabilityPlay": empty_deck_probability_play,
    "RankHint": rank_hint,
    "StackDefenseHint": stack_defense_hint,
    "FutureStackDefenseHint": future_stack_defense_hint,
    "FullProbabilityPlay": full_probability_play,
    "FullEmptyDeckProbabilityPlay": full_empty_deck_probability_play
}

state_action_map = {
    "OldestDiscard": oldest_discard
}

cards_per_rank = {
    0: 3,
    1: 2,
    2: 2,
    3: 2,
    4: 1,
    5: 0
}

game_colors = ['B', 'G', 'R', 'W', 'Y']
