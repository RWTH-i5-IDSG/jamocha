(deftemplate ip-state 
	(slot protocol-name) 
	(slot state-name)
)

(deftemplate ip-transition
	(slot from-state)
	(slot to-state)
	(slot speechact)
	(slot agent-type)
)

(deftemplate ip-run
	(slot conversation-id)
	(slot current-state)
	(slot initiator)
	(slot participant)
)


(defrule ip-check-from-participant
	; Hole die nötigen Informationen aus der neuen Nachricht
	(agent-message
		(sender ?sender)
		(receiver $?receiver)
		(performative ?performative)
		(conversation-id ?conversation-id)
	)
	; Lese den aktuellen Status des Automatenlaufs aus
	?run <- (ip-run
		(conversation-id ?conversation-id)
		(current-state ?current-state)
		(participant ?sender)
		(initiator ?otherAgent)
	)
	; Suche des Folgezustands
	(ip-transition
		(from-state ?current-state)
		(to-state ?state)
		(speechact ?performative)
		(agent-type "participant")
	)
	; Teste, ob einer der Receiver der Initiator des Laufs ist
	(test (> (member$ ?otherAgent $?receiver) 0))
	=>
	; Lasse den Automaten einen Schritt laufen
	(modify ?run (current-state ?state))
)

(defrule ip-check-from-initiator
	; Hole die nötigen Informationen aus der neuen Nachricht
	(agent-message
		(sender ?sender)
		(receiver $?receiver)
		(performative ?performative)
		(conversation-id ?conversation-id)
	)
	; Lese den aktuellen Status des Automatenlaufs aus
	?run <- (ip-run
		(conversation-id ?conversation-id)
		(current-state ?current-state)
		(participant ?otherAgent)
		(initiator ?sender)
	)
	; Suche des Folgezustands
	(ip-transition
		(from-state ?current-state)
		(to-state ?state)
		(speechact ?performative)
		(agent-type "initiator")
	)
	; Teste, ob einer der Receiver der Participant des Laufs ist
	(test (> (member$ ?otherAgent $?receiver) 0))
	=>
	; Lasse den Automaten einen Schritt laufen
	(modify ?run (current-state ?state))
)

(defrule ip-start-initiator
	; Hole die nötigen Informationen aus der neuen Nachricht
	(agent-message
		(sender ?sender)
		(receiver $?receiver)
		(protocol ?protocol)
		(conversation-id ?conversation-id)
	)
	; Finden des Startzustands
	?start-state <- (ip-state
		(protocol-name ?protocol)
		(state-name 0)
	)
	; Der Sender ist lokal, also der Initiator
	(agent-is-local
		(agent ?sender)
	)
	; Es existiert noch kein Lauf zu der angegebenen conversation-id
	(not
		(ip-run
			(conversation-id ?conversation-id)
		)
	)
	=> 
	; Für jeden Empfänger der Nachricht wird ein Lauf angelegt
	(bind $?rest $?receiver)
	(while (greater (length$ $?rest) 0) do
		(assert
			(ip-run
				(conversation-id ?conversation-id)
				(current-state ?start-state)
				(initiator ?sender)
				(participant (first$ $?rest))
			)
		)
		(bind $?rest (rest$ $?rest))
	)
)


(defrule ip-start-participant
	; Hole die nötigen Informationen aus der neuen Nachricht
	(agent-message
		(sender ?sender)
		(receiver $?receiver)
		(protocol ?protocol)
		(conversation-id ?conversation-id)
	)
	; Finden des Startzustands
	?start-state <- (ip-state
		(protocol-name ?protocol)
		(state-name 0)
	)
	; Suche lokale Agenten
	(agent-is-local
		(agent ?agent)
	)
	; Es existiert noch kein Lauf zu der angegebenen conversation-id
	(not
		(ip-run
			(conversation-id ?conversation-id)
		)
	)
	; Teste ob der lokale Agent zu den Empfängern gehört, also Participant ist
	(test (> (member$ ?agent $?receiver) 0))
	=> 
	; Lege nur für den lokale Agenten einen Lauf an
	(assert
		(ip-run
			(conversation-id ?conversation-id)
			(current-state ?start-state)
			(initiator ?sender)
			(participant ?agent)
		)
	)
)