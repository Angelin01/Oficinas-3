

def rotate_list_right(l: list):
    l.insert(0, l.pop(-1))


def rotate_list_left(l: list):
    l.append(l.pop(0))


def sep_bar_levels(level, max_intensity):

    div, mod = divmod(level, max_intensity)

    bar = []

    for d in range(int(div)):
        bar.append(max_intensity)

    if div < 3:
        bar.append(mod)

    len_bar = len(bar)
    while len_bar < 3:
        bar.append(0)
        len_bar += 1

    return bar


def norm(value, source_min, source_max):
    return (value - source_min) / (source_max - source_min)


def value_map(value, source_min, source_max, target_min, target_max):
    t = (value - source_min) / (source_max - source_min)
    return (1 - t) * target_min + t * target_max

def value_clamp(value, c_min, c_max):
    if value < c_min:
        return c_min

    if value > c_max:
        return c_max

    return value