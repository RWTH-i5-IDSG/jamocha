;default file:
(batch /Users/amufsuism/Documents/eclipse_workspace/jamocha/apps/jamochaagent/samples/MissManners/common.clp)


(bind ?*Gender* "w")

(deffunction SetGender
	(?Gender)
	(bind ?*Gender* ?Gender)
)


(deffunction register
;params:
	(?partyHost ?hobbies)
	
	(bind ?announcementFact
		(assert
			(PartyAnnouncement
				(name (local-agent-name))
				(address (agent-name))
				(sex ?*Gender*)
				(hobbies ?hobbies)
			)
		)
	)
	
	(bind ?content (str-cat
		"((action (agent-identifier :name "
		(clips2sl ?partyHost)
		")"
		"(resolvePartyAnnouncement "
		(clips2sl ?announcementFact)
		")))"
   ))
	(assert 
		(agent-message
			(receiver ?partyHost)
			(performative "request")
			(protocol "fipa-request")
			(content ?content)
			(language "fipa-sl")
			(encoding "")
			(ontology "MissManners")
			(conversation-id (str-cat (agent-name) (ms-time)))
			(in-reply-to "")
			(reply-with "123")
			(reply-by 0)
			(timestamp (datetime2timestamp (now)))
			(incoming FALSE)
		)
	)
	(fire)
)


(deffunction registerNow
()
(register "MissManners@amufusims-mac.local:1099/JADE"  (create$ "wurst" "bier" "senf"))
)

(deffunction resolveSeatingResults
(?assSeat)
(printout t "Got result, my seatnumber is:" (fact-slot-value ?assSeat "seatNumber"))
)