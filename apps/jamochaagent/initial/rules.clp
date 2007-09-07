; ===================================================
; Definition of rules that are needed.
; ===================================================

(defrule incoming-message
	"Fires when a message in FIPA-SL arrives, that is addressed to a local Agent."
	; Only look for messages addressed to the local agent.
	(agent-identifier
		(name ?receiver)
		(local TRUE)
	)
	; Only extract messages that have not been answered yet and that are incoming.
	?message <- (agent-message
		(receiver $?receivers)
		(language "fipa-sl")
		(protocol ?protocol)
		(incoming TRUE)
		(processed FALSE)
		(is-template FALSE)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver $?receivers) 0))
	
	=>
	
	; Process the message.
	(process-incoming-message ?message)
)


(defrule outgoing-message
	"Fires when a message in FIPA-SL has to be send."
	; Only extract messages that have not been sent yet and that are outgoing.
	?message <- (agent-message
		(language "fipa-sl")
		(incoming FALSE)
		(processed FALSE)
		(is-template FALSE)
	)
	
	=>
	
	; Process the message.
	(process-outgoing-message ?message)
)