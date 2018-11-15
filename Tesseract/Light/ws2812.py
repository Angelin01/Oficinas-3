#!/usr/bin/python
NumpyImported = False
try:
    import numpy
    from numpy import sin, cos, pi

    NumpyImported = True
except ImportError:
    # print("Warning: no numpy found, routines will be slow")
    pass
"""
T0H: 0.35   -> 2p=0.31  3p=0.47
T0L: 0.80   -> 6p=0.94  5p=0.78
T1H: 0.70   -> 4p=0.625 5p=0.78
T1L: 0.60   -> 4p=0.625 3p=0.47
"""


def write2812_numpy8(spi, data):
    d = numpy.array(data).ravel()
    tx = numpy.zeros(len(d) * 8, dtype=numpy.uint8)
    for ibit in range(8):
        # print ibit
        # print ((d>>ibit)&1)
        # tx[7-ibit::8]=((d>>ibit)&1)*0x18 + 0xE0   #0->3/5, 1-> 5/3
        # tx[7-ibit::8]=((d>>ibit)&1)*0x38 + 0xC0   #0->2/6, 1-> 5/3
        tx[7 - ibit::8] = ((d >> ibit) & 1) * 0x78 + 0x80  # 0->1/7, 1-> 5/3
    # print [hex(v) for v in tx]
    # print [hex(v) for v in tx]
    spi.xfer(tx.tolist(), int(8 / 1.25e-6))


# spi.xfer(tx.tolist(), int(8e6))


def write2812_numpy4(spi, data):
    d = numpy.array(data).ravel()

    tx = numpy.zeros(len(d) * 4, dtype=numpy.uint8)

    for i_bit in range(4):
        tx[3 - i_bit::4] = ((d >> (2 * i_bit + 1)) & 1) * 0x60 + ((d >> (2 * i_bit + 0)) & 1) * 0x06 + 0x88

    # .95 and .90 work too.
    spi.xfer(tx.tolist(), int(4 / 1.05e-6))  # works, no flashes on Zero, Works on Raspberry 3


if NumpyImported:
    write2812 = write2812_numpy4
else:
    raise ImportError("Does not have Numpy!")
