(deftemplate game
	(slot gameNumber (type LONG)(default 0))
	(slot oponent (type STRING))
	(slot maxValue (type LONG))
	(slot lastValue (type LONG)(default -1))
	(slot maxGuessing (type LONG)(default -1))
	(slot guessingCount (type LONG)(default 0))
	(slot gameFinished (type BOOLEAN)(default false))
)

(deftemplate newGame
	(slot maxValue (type LONG)(default 1000))
	(slot maxGuessing (type LONG)(default 0))
	(slot agentName (type STRING))
)

(deffunction new-conversation-id
	"Generates a new conversation-id."
	(functiongroup NumberGuessing)
	()
	(return (str-cat (agent-name) (ms-time)))
)