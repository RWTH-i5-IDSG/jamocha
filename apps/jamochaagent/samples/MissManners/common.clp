(deftemplate PartyAnnouncement
	(slot name 	(type STRING) (default ?NONE))
	(slot address (type STRING) (default ?NONE))
	(slot sex 	(type STRING) (default ?NONE))
	(multislot hobbies)
)
(deftemplate AssignedSeat
	(slot name (type STRING) (default ?NONE))
	(slot seatNumber (type LONG) (default ?NONE))
)