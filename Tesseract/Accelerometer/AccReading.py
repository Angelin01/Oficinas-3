from enum import Enum

class AccReading(Enum):
	NONE = 0,
	INC_RIGHT = 1, 
	INC_LEFT = 2,
	INC_FRONT = 3,
	INC_BACK = 4,
	UP_DOWN = 5,
	AGITATION = 6