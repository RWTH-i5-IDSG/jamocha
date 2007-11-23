(deftemplate ip-state 
	(slot protocol-name) 
	(slot state-name) 
	(slot incoming-speechact) 
	(slot agent-type) 
	(slot previous-state) 
)

(deftemplate ip-run 
	(slot conversation-id) 
	(slot current-state)
)

(deftemplate ip-run-role 
	(slot ip-run) 
	(slot agent-type) 
	(slot agent) 
) 


(defrule ip-check-correct 
	; Hole die n�tigen Informationen aus der neuen Nachricht 
	(agent-message 
		(sender ?sender) 
		(receiver $?receiver) 
		(performative ?performative) 
		(protocol ?protocol) 
		(conversation-id ?conversation-id) 
	) 
	; Lese den aktuellen Status des Automatenlaufs aus 
	?run <- (ip-run 
		(conversation-id ?conversation-id) 
		(current-state ?current-state) 
	) 
	; Suche des Folgezustands 
	?state <- (ip-state 
		(protocol-name ?protocol) 
		(incoming-speechact ?performative) 
		(agent-type ?agent-type) 
		(previous-state ?current-state) 
	) 
	; �berpr�fe, ob der richtige Agententyp in diesem Lauf gesendet hat 
	(ip-run-role 
		(ip-run ?run) 
		(agent-type ?agent-type) 
		(agent ?sender) 
	) 
	=> 
	; Lasse den Automaten einen Schritt laufen 
	(modify ?run (current-state ?state)) 
) 

(defrule ip-start
	; Hole die n�tigen Informationen aus der neuen Nachricht 
	(agent-message 
		(sender ?sender) 
		(receiver $?receiver) 
		(performative ?performative) 
		(protocol ?protocol) 
		(conversation-id ?conversation-id) 
	)
	; Finden des Startzustands
	?startState <- (ip-state 
		(protocol-name ?protocol) 
		(incoming-speechact NIL)
		(previous-state NIL) 
	)
	; �berpr�fen, ob der Sprechakt g�ltig ist
	?nextState <- (ip-state 
		(protocol-name ?protocol) 
		(incoming-speechact ?performative)
		(previous-state ?startState) 
	)
	; Es existiert noch kein Lauf zu der angegebenen conversation-id
	(not
		(ip-run 
			(conversation-id ?conversation-id)
		)
	)
	=>
	; Der neue Lauf wird angelegt
	(bind ?run
		(assert
			(ip-run
				(conversation-id ?conversation-id)
				(current-state ?startState)
			)
		)
	)
	; Der Sender der Nachricht wird als Initiator hinzugef�gt
	(assert
		(ip-run-role
			(ip-run ?run) 
			(agent-type "initiator") 
			(agent ?sender)
		)
	)
	; Alle Empf�nger der Nachricht werden als Participant des Laufs hinzugef�gt
	(bind $?rest $?receiver)
	(while (greater (length$ $?rest) 0 ) do
		(assert
			(ip-run-role
				(ip-run ?run) 
				(agent-type "participant") 
				(agent (first$ $?rest))
			)
		)
		(bind $?rest (rest$ $?rest))
	)
)