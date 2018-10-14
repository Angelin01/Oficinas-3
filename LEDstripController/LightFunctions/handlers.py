def standard_handler(values_sequence, step):
    """
    Simple handler, just returns the sequence for the specific step
    :param values_sequence: a johnson_gradient value sequence.
    :param step: Which of the values to return in the sequence.
    :return: The sequence to use for the LEDs.
    """
    return values_sequence[step]

