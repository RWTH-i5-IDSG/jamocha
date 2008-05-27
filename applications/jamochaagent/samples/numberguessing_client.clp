(batch apps/jamochaagent/samples/numberguessing_commondata.clp)

(deffunction new-game
	"Start a new game with an agent using the given max value and given max guessing count. Start this for the show!"
	(functiongroup NumberGuessing)
	(?oponent ?maxValue ?maxGuessing)
	(bind ?newGame 
		(assert
			(newGame
				(maxValue ?maxValue)
				(maxGuessing ?maxGuessing)
				(agentName (agent-name))
			)
		)
	)
	(bind ?content (str-cat
		"((action (agent-identifier :name "
		(clips2sl ?oponent)
       	")"
     	"(start-game "
     	(clips2sl ?newGame)
     	")))"
    ))
	(assert
		(agent-message
			(receiver (create$ ?oponent))
			(performative "request")
			(content ?content)
			(language "fipa-sl")
			(ontology "numberguessing")
			(protocol "fipa-request")
			(conversation-id (new-conversation-id))
			(reply-with "initialization phase")
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		)
	)
	(fire)
)