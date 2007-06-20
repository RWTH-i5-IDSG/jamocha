(deftemplate piece
  (slot name (type STRING))
  (slot id (type STRING))
  (slot inPlay (type BOOLEAN))
  (slot color (type STRING))
  (slot blocked (type BOOLEAN))
  (slot currentPosition (type STRING))
  (slot lastPosition (type STRING))
)
(deftemplate capture
  (slot attacker (type STRING))
  (slot defender (type STRING))
  (slot startPosition (type STRING))
  (slot endPosition (type STRING))
  (slot weight (type INTEGER))
  (slot color (type STRING))
)
(deftemplate move
  (slot piece (type STRING))
  (slot color (type STRING))
  (slot startPosition (type STRING))
  (slot endPosition (type STRING))
)
(deftemplate position
  (slot piece (type STRING))
  (slot lastpiece (type STRING))
  (slot cellId (type STRING))
)
(deftemplate turn
  (slot color (type STRING))
)

(defrule capture1
  (piece
    (currentPosition ?curr)
    (id ?target)
    (color ?color)
  )
  (turn
    (color ~?color)
  )
  (move
    (endPosition ?curr)
    (startPosition ?startpos)
    (piece ?piece)
    (color ~?color)
    (color ?mcolor)
  )
=>
  ;; assert a new capture fact to the working memory of the moves that can capture
  (assert 
    (capture 
      (attacker ?piece)
      (defender ?target)
      (startPosition ?startpos)
      (endPosition ?curr)
      (weight 0)
      (color ?mcolor)
    )
  )
  (printout t "(capture (attacker " ?piece ") (defender " 
    ?target ")(startPosition " ?startpos ")(endPosition " ?curr ")(weight 0) )" crlf)
)
(defrule rankcapture1
  ?cap <- (capture
    (defender "bc1"|"bc2"|"bb1"|"bb2"|"bn1"|"bn2"|"bq"|"bk"|"wc1"|"wc2"|"wb1"|"wb2"|"wn1"|"wn2"|"wq"|"wk")
    (weight 0)
  )
=>
  (modify ?cap (weight 10) )
  (printout t "rank the move" crlf)
)
(defrule isCheck
  ?cap <- (capture
    (defender ~"bk" & ~"wk")
  )
=>
  (printout t "no king is in check" crlf)
)

(assert (turn (color "b") ) )
(assert (position (piece "wn1")(cellId "c3")(color "w") ) )
(assert (position (piece "bb2")(cellId "a5")(color "w") ) )
(assert (position (piece "bc2")(cellId "h8")(color "b") ) )
(assert (position (piece "wp8")(cellId "h4")(color "b") ) )
(assert (move (piece "bb2")(startPosition "a5")(endPosition "c3")(color "b") ) )
(assert (move (piece "bb2")(startPosition "a5")(endPosition "c7")(color "b") ) )
(assert (move (piece "bb2")(startPosition "a5")(endPosition "b4")(color "b") ) )
(assert (move (piece "bb2")(startPosition "a5")(endPosition "b6")(color "b") ) )
(assert (move (piece "bc2")(startPosition "h8")(endPosition "g8")(color "b") ) )
(assert (move (piece "bc2")(startPosition "h8")(endPosition "h7")(color "b") ) )
(assert (move (piece "bc2")(startPosition "h8")(endPosition "h6")(color "b") ) )
(assert (move (piece "bc2")(startPosition "h8")(endPosition "h5")(color "b") ) )
(assert (move (piece "bc2")(startPosition "h8")(endPosition "h4")(color "b") ) )
(assert (piece (name "white knight 1")(id "wn1")(inPlay true)(color "w")(blocked false)(currentPosition "c3") ) )
(assert (piece (name "white pawn 8")(id "wp8")(inPlay true)(color "w")(blocked false)(currentPosition "h4") ) )
(assert (piece (name "black bishop 2")(id "bb2")(inPlay true)(color "b")(blocked false)(currentPosition "a5") ) )
(assert (piece (name "black castle 2")(id "bc2")(inPlay true)(color "b")(blocked false)(currentPosition "h8") ) )
(fire)
