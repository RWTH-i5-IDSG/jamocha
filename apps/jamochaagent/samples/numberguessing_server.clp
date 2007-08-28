(batch apps/jamochaagent/samples/numberguessing_commondata.clp)

(bind ?*lastGameNumber* 0)

(deftemplate solution
	(slot game)
	(slot number (type LONG))
)

(deffunction get-number
	"function that returns a new (pseudo) random int between 0 and a max value."
	(functiongroup NumberGuessing)
	(?maxValue)
	(round (* (random) ?maxValue))
)

(deffunction start-game
	"function for a request to start a new game."
	(functiongroup NumberGuessing)
	(?newGameFact)
	(bind ?*lastGameNumber* (+ ?*lastGameNumber* 1))
	(bind ?game
		(assert
			(game
				(gameNumber ?*lastGameNumber*)
				(oponent (fact-slot-value ?newGameFact "agentName"))
				(maxValue (fact-slot-value ?newGameFact "maxValue"))
				(maxGuessing (fact-slot-value ?newGameFact "maxGuessing"))
			)
		)
	)
	(assert
		(solution
			(game ?game)
			(number (get-number (fact-slot-value ?newGameFact "maxValue")))
		)
	)
	(return (str-cat "\"" ?*lastGameNumber* "\""))
)

; (new-game "server@nocturna.local:1099/JADE" 1000 0)
