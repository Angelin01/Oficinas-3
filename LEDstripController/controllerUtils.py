

def rotate_list_right(l: list):
    l.insert(0, l.pop(-1))


def rotate_list_left(l: list):
    l.append(l.pop(0))


def sep_bar_levels(level, max_intensity):

    div, mod = divmod(level, max_intensity)

    bar = []

    for d in range(div):
        bar.append(max_intensity)

    if div < 3:
        bar.append(mod)

    len_bar = len(bar)
    while len_bar < 3:
        bar.append(0)
        len_bar += 1

    return bar
