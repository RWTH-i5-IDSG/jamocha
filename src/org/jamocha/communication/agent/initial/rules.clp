; ===================================================
; Definition of rules that are needed.
; ===================================================

(defrule incoming-message
	"Fires when a message in FIPA-SL arrives, that is addressed to a local Agent."
	; Only extract messages that have not been answered yet and that are incoming.
	?message <- (agent-message
		(receiver $?receivers)
		(language ?language)
		(protocol ?protocol)
		(incoming TRUE)
		(processed FALSE)
		(is-template FALSE)
	)
	; Only look for messages addressed to the local agent.
	(agent-is-local
		(agent ?receiver)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver $?receivers) 0))
	(test (eq (str-lower ?language) "fipa-sl"))
	=>
	
	; Process the message.
	(process-incoming-message ?message)
)


(defrule outgoing-message
	"Fires when a message in FIPA-SL has to be send."
	(declare
		(salience 200)
	)
	; Only extract messages that have not been sent yet and that are outgoing.
	?message <- (agent-message
		;(language ?language)
		(incoming FALSE)
		(processed FALSE)
		(is-template FALSE)
	)
	(agent-is-local
		(agent ?sender)
	)
	;(test (eq (str-lower ?language) "fipa-sl"))
	
	=>
	
	; Process the message.
	(process-outgoing-message ?message ?sender)
)