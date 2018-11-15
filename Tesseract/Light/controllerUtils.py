

def rotate_list_right(l: list):
    l.insert(0, l.pop(-1))


def rotate_list_left(l: list):
    l.append(l.pop(0))
