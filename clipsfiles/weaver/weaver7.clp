

(defrule p120
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay1) (compo ?cpo1) (commo ?cmo1))
  (not (horizontal (net-name nil) (com ?com1)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw86&:(> ?qw86 ?com1)) (com ?qw87&:(> ?qw87 ?max1))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qz22&:(< ?qz22 ?min1))))
  ?v1 <-(vertical (net-name nil) (min ?min2&:(< ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?com1) (> ?max2 ?min2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1))) (layer ?lay2&~?lay1) (compo ?cpo2) (commo ?cmo2))
  (not (vertical (net-name nil) (min ?qz23&:(< ?qz23 ?com1)) (max ?qw89&:(> ?qw89 ?com1)) (com ?qw60&:(and (>= ?qw60 ?min1) (<= ?qw60 ?max1))) (layer ?lay2)))
  ?t1 <-  (to-be-routed (net-name ?nn2&~?nn1) (no-of-attached-pins ?nap))
  (not (vertical (status nil) (net-name ?nn2&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (pin (net-name ?nn2) (pin-y ?qz24&:(< ?qz24 ?com1)))
  (pin (net-name ?nn2) (pin-y ?qw90&:(> ?qw90 ?com1)))
  ?b1 <- (branch-no ?pn)
  =>
  (assert (branch-no =(+ ?pn 1)))
  (modify ?t1 (no-of-attached-pins =(- ?nap 1)))
  (retract ?b1)
  (assert (vertical (min ?cmo1) (max ?cpo1) (com ?com2) (layer ?lay2) (net-name ?nn2) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (vertical (min ?min2) (max ?cmo1) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (max-net ?nn2)))
  (assert (vertical (min ?cpo1) (max ?max2) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (min-net ?nn2)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?com2) (grid-y ?cmo1) (grid-layer ?lay2) (came-from north)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?com2) (grid-y ?cpo1) (grid-layer ?lay2) (came-from south)))
  (retract ?v1)
)

(defrule p121
  (context (present propagate-constraint))
  (horizontal (net-name ?nn1&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay1) (compo ?cpo1) (commo ?cmo1))
  (not (horizontal (net-name nil) (com ?com1)))
  ?v1 <-(vertical (net-name nil) (min ?min2&:(< ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?com1) (> ?max2 ?min2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1))) (layer ?lay2&~?lay1) (compo ?cpo2) (commo ?cmo2))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw86&:(> ?qw86 ?com1)) (com ?qw87&:(> ?qw87 ?com2))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qz22&:(< ?qz22 ?com2))))
  ?t1 <-  (to-be-routed (net-name ?nn2&~?nn1) (no-of-attached-pins ?nap))
  (not (vertical (status nil) (net-name ?nn2&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (pin (net-name ?nn2) (pin-y ?qz23&:(< ?qz23 ?com1)))
  (pin (net-name ?nn2) (pin-y ?qw89&:(> ?qw89 ?com1)))
  ?b1 <- (branch-no ?pn)
  =>
  (assert (branch-no =(+ ?pn 1)))
  (modify ?t1 (no-of-attached-pins =(- ?nap 1)))
  (retract ?b1)
  (assert (vertical (min ?cmo1) (max ?cpo1) (com ?com2) (layer ?lay2) (net-name ?nn2) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (vertical (min ?min2) (max ?cmo1) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (max-net ?nn2)))
  (assert (vertical (min ?cpo1) (max ?max2) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (min-net ?nn2)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?com2) (grid-y ?cmo1) (grid-layer ?lay2) (came-from north)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?com2) (grid-y ?cpo1) (grid-layer ?lay2) (came-from south)))
  (retract ?v1)
)

(defrule p122
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?min2) (max ?max2&:(> ?max2 ?min2)) (com ?com2) (layer ?lay2) (compo ?cpo2) (commo ?cmo2))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?min2)) (max ?qw62&:(>= ?qw62 ?max2)) (com ?qw86&:(> ?qw86 ?com2))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?min2)) (max ?qw63&:(>= ?qw63 ?max2)) (com ?qz21&:(< ?qz21 ?com2))))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?min2)) (max ?qw64&:(>= ?qw64 ?max2)) (com ?com2) (layer ~?lay2)))
  (congestion (direction col) (coordinate ?x1&:(<= ?x1 ?max2)) (como ?x2&:(>= ?x2 ?min2)))
  (not (horizontal (net-name nil) (min ?qw77&:(<= ?qw77 ?x2)) (max ?qw65&:(>= ?qw65 ?x1)) (com ?qw87&:(> ?qw87 ?com2))))
  (not (horizontal (net-name nil) (min ?qw78&:(<= ?qw78 ?x2)) (max ?qw66&:(>= ?qw66 ?x1)) (com ?qz22&:(< ?qz22 ?com2))))
  (not (horizontal (net-name nil) (min ?qw79&:(<= ?qw79 ?x2)) (max ?qw67&:(>= ?qw67 ?x1)) (com ?com2) (layer ~?lay2)))
  ?t1 <-  (to-be-routed (net-name ?nn2) (no-of-attached-pins ?nap))
  (not (horizontal (status nil) (net-name ?nn2&~nil) (min ?qw80&:(<= ?qw80 ?x2)) (max ?qw68&:(>= ?qw68 ?x1))))
  (pin (net-name ?nn2) (pin-x ?qw81&:(<= ?qw81 ?x2)))
  (pin (net-name ?nn2) (pin-x ?qw69&:(>= ?qw69 ?x1)))
  ?b1 <- (branch-no ?pn)
  (not (horizontal (net-name ~?nn2&~nil) (min ?qw82&:(<= ?qw82 ?x2)) (max ?qw70&:(>= ?qw70 ?x2)) (com ?com2) (layer ?lay2)))
  (not (vertical (net-name ~?nn2&~nil) (min ?qw83&:(<= ?qw83 ?com2)) (max ?qw71&:(>= ?qw71 ?com2)) (com ?x2) (layer ?lay2)))
  (not (horizontal (net-name ~?nn2&~nil) (min ?qw84&:(<= ?qw84 ?x1)) (max ?qw72&:(>= ?qw72 ?x1)) (com ?com2) (layer ?lay2)))
  (not (vertical (net-name ~?nn2&~nil) (min ?qw85&:(<= ?qw85 ?com2)) (max ?qw73&:(>= ?qw73 ?com2)) (com ?x1) (layer ?lay2)))
  =>
  (assert (branch-no =(+ ?pn 1)))
  (modify ?t1 (no-of-attached-pins =(- ?nap 1)))
  (retract ?b1)
  (assert (horizontal (min ?x2) (max ?x1) (com ?com2) (layer ?lay2) (net-name ?nn2) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (horizontal (min ?min2) (max ?x2) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (max-net ?nn2)))
  (assert (horizontal (min ?x1) (max ?max2) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (min-net ?nn2)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?x2) (grid-y ?com2) (grid-layer ?lay2) (came-from east)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?x1) (grid-y ?com2) (grid-layer ?lay2) (came-from west)))
  (retract ?h1)
)

(defrule p123
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?min1) (max ?max1&:(> ?max1 ?min1)) (com ?com1&~1) (layer ?lay1) (compo ?cpo1) (commo ?cmo1) (min-net ?mnn) (max-net ?xnn))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw86&:(> ?qw86 ?com1))))
  (horizontal (net-name nil) (min ?com1) (max ?garb3&:(> ?garb3 ?com1)) (com ?bhcom&:(and (<= ?bhcom ?max1) (>= ?bhcom ?min1))) (layer ?garb4))
  (horizontal (net-name nil) (min ?garb1&:(< ?garb1 ?com1)) (max ?com1) (com ?thcom&:(and (<= ?thcom ?max1) (> ?thcom ?bhcom))) (layer ?garb2))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?com1) (com ?qz61&:(and (< ?qz61 ?thcom) (> ?qz61 ?bhcom)))))
  (not (horizontal (net-name nil) (min ?com1) (max ?qw87&:(> ?qw87 ?com1)) (com ?qz62&:(and (< ?qz62 ?thcom) (> ?qz62 ?bhcom)))))
  ?t1 <-  (to-be-routed (net-name ?nn2) (no-of-attached-pins ?nap))
  (not (vertical (status nil) (net-name ?nn2&~nil) (com ?com1)))
  (not (horizontal (status nil) (net-name ?nn2&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (pin (net-name ?nn2) (pin-x ?qz22&:(< ?qz22 ?com1)))
  (pin (net-name ?nn2) (pin-x ?qw88&:(> ?qw88 ?com1)))
  ?b1 <- (branch-no ?pn)
  =>
  (assert (branch-no =(+ ?pn 1)))
  (modify ?t1 (no-of-attached-pins =(- ?nap 1)))
  (retract ?v1 ?b1)
  (assert (vertical (min ?min1) (max ?bhcom) (com ?com1) (layer ?lay1) (commo ?cmo1) (compo ?cpo1) (max-net ?nn2) (min-net ?mnn)))
  (assert (vertical (min ?thcom) (max ?max1) (com ?com1) (layer ?lay1) (commo ?cmo1) (compo ?cpo1) (min-net ?nn2) (max-net ?xnn)))
  (assert (vertical (min ?bhcom) (max ?thcom) (com ?com1) (layer ?lay1) (commo ?cmo1) (compo ?cpo1) (net-name ?nn2) (pin-name ?pn)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?com1) (grid-y ?bhcom) (grid-layer ?lay1) (came-from north)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?com1) (grid-y ?thcom) (grid-layer ?lay1) (came-from south)))
)

(defrule p124
  (context (present propagate-constraint))
  (ff (net-name ?nn1) (grid-x ?gx) (grid-y ?com2) (grid-layer ?lay1) (pin-name ?egarb1))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com2)) (max ?qw62&:(>= ?qw62 ?com2)) (com ?gx)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qw87&:(> ?qw87 ?com2))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw88&:(> ?qw88 ?gx)) (com ?qz22&:(< ?qz22 ?com2))))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (min ?qz23&:(< ?qz23 ?gx))))
  (not (vertical (status nil) (net-name ?nn1&~nil) (com ?qz24&:(< ?qz24 ?gx))))
  ?h1 <- (horizontal (net-name nil) (min ?min2&:(< ?min2 ?gx)) (max ?max2&:(and (>= ?max2 ?gx) (> ?max2 ?min2))) (com ?com2) (layer ?lay1) (compo ?cpo2) (commo ?cmo2) (min-net ?nn2))
  =>
  (assert (horizontal (min ?min2) (max =(- ?gx 1)) (com ?com2) (layer ?lay1) (commo ?cmo2) (compo ?cpo2) (min-net ?nn2)))
  (modify ?h1 (min ?gx) (min-net ?nn1))
)

(defrule p125
  (context (present propagate-constraint))
  (ff (net-name ?nn1) (grid-x ?gx) (grid-y ?com2) (grid-layer ?lay1) (pin-name ?egarb1))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com2)) (max ?qw62&:(>= ?qw62 ?com2)) (com ?gx)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?qw88&:(> ?qw88 ?com2))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qz22&:(< ?qz22 ?com2))))
  (not (horizontal (status nil) (net-name ?nn1&~nil) (max ?qw89&:(> ?qw89 ?gx))))
  (not (vertical (status nil) (net-name ?nn1&~nil) (com ?qw90&:(> ?qw90 ?gx))))
  ?h1 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?gx)) (max ?max2&:(and (> ?max2 ?gx) (> ?max2 ?min2))) (com ?com2) (layer ?lay1) (compo ?cpo2) (commo ?cmo2) (max-net ?nn2))
  =>
  (assert (horizontal (min =(+ ?gx 1)) (max ?max2) (com ?com2) (layer ?lay1) (commo ?cmo2) (compo ?cpo2) (max-net ?nn2)))
  (modify ?h1 (max ?gx) (max-net ?nn1))
)

(defrule p126
  (context (present propagate-constraint | extend-total-verti))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~east) (net-name ?nn) (grid-x ?max1) (grid-y ?com1) (grid-layer ?lay) (pin-name ?pn))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?garb1&:(> ?garb1 ?max1)) (com ?com1) (layer ?lay) (compo ?cpo1) (commo ?cmo1))
  (not (horizontal (net-name nil) (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?com1) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1)))
  =>
  (modify ?h1 (min =(+ ?max1 1)) (min-net ?nn))
  (assert (horizontal (min ?max1) (max =(+ ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo1) (compo ?cpo1)))
  (modify ?ff1 (grid-x =(+ ?max1 1)) (came-from west) (can-chng-layer nil))
)

(defrule p127
  (context (present propagate-constraint | extend-total-verti))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~west) (net-name ?nn) (grid-x ?max1) (grid-y ?com1) (grid-layer ?lay) (pin-name ?pn))
  ?h1 <- (horizontal (net-name nil) (min ?garb1) (max ?max1&:(> ?max1 ?garb1)) (com ?com1) (layer ?lay) (compo ?cpo1) (commo ?cmo1))
  (not (horizontal (net-name nil) (min ?max1) (max ?qw86&:(> ?qw86 ?max1)) (com ?com1) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1)))
  =>
  (modify ?h1 (max =(- ?max1 1)) (max-net ?nn))
  (assert (horizontal (max ?max1) (min =(- ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo1) (compo ?cpo1)))
  (modify ?ff1 (grid-x =(- ?max1 1)) (came-from east) (can-chng-layer nil))
)

(defrule p128
  (context (present propagate-constraint | extend-total-verti))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~north) (net-name ?nn) (grid-x ?com1) (grid-y ?max1) (grid-layer ?lay) (pin-name ?pn))
  ?v1 <-(vertical (net-name nil) (min ?max1) (max ?garb1&:(> ?garb1 ?max1)) (com ?com1) (layer ?lay) (compo ?cpo1) (commo ?cmo1))
  (not (vertical (net-name nil) (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?com1) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1)))
  =>
  (modify ?v1 (min =(+ ?max1 1)) (min-net ?nn))
  (assert (vertical (min ?max1) (max =(+ ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo1) (compo ?cpo1)))
  (modify ?ff1 (grid-y =(+ ?max1 1)) (came-from south) (can-chng-layer nil))
)

(defrule p129
  (context (present propagate-constraint | extend-total-verti))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~south) (net-name ?nn) (grid-x ?com1) (grid-y ?max1) (grid-layer ?lay) (pin-name ?pn))
  ?v1 <-(vertical (net-name nil) (min ?garb1) (max ?max1&:(> ?max1 ?garb1)) (com ?com1) (layer ?lay) (compo ?cpo1) (commo ?cmo1))
  (not (vertical (net-name nil) (min ?max1) (max ?qw86&:(> ?qw86 ?max1)) (com ?com1) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1)))
  =>
  (modify ?v1 (max =(- ?max1 1)) (max-net ?nn))
  (assert (vertical (max ?max1) (min =(- ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo1) (compo ?cpo1)))
  (modify ?ff1 (grid-y =(- ?max1 1)) (came-from north) (can-chng-layer nil))
)

(defrule p130
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?h1 <- (horizontal (status nil) (net-name ?nn) (min ?min) (max ?max&:(> ?max ?min)) (com ?com) (layer ?lay) (pin-name ?pn) (max-net ?gar1))
  ?h2 <- (horizontal (status nil) (net-name ?nn) (min ?max) (max ?max2&:(> ?max2 ?max)) (com ?com) (layer ?lay) (pin-name ?pn) (max-net ?nn1))
  =>
  (modify ?h1 (max ?max2) (max-net ?nn1))
  (retract ?h2)
)

(defrule p131
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?v1 <-(vertical (status nil) (net-name ?nn) (min ?min) (max ?max&:(> ?max ?min)) (com ?com) (layer ?lay) (pin-name ?pn) (max-net ?gar1))
  ?v2 <- (vertical (status nil) (net-name ?nn) (min ?max) (max ?max2&:(> ?max2 ?max)) (com ?com) (layer ?lay) (pin-name ?pn) (max-net ?nn1))
  =>
  (modify ?v1 (max ?max2) (max-net ?nn1))
  (retract ?v2)
)

(defrule p132
  (context (present propagate-constraint))
  ?h1 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?gar1) (max ?max) (com ?com) (layer ?lay) (pin-name ?pn))
  ?h2 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?max) (max ?max2) (com ?com) (layer ?lay) (pin-name ?pn1&~?pn))
  (vertical (status nil) (net-name ?nn&~nil) (pin-name ?pn1))
  =>
  (modify ?h1 (max ?max2))
  (retract ?h2)
)

(defrule p133
  (context (present propagate-constraint))
  ?h1 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?min) (max ?gar1) (com ?com) (layer ?lay) (pin-name ?pn))
  ?h2 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?min2) (max ?min) (com ?com) (layer ?lay) (pin-name ?pn1&~?pn))
  (vertical (status nil) (net-name ?nn&~nil) (pin-name ?pn1))
  =>
  (modify ?h1 (min ?min2))
  (retract ?h2)
)

(defrule p134
  (context (present propagate-constraint))
  ?v1 <-(vertical (status nil) (net-name ?nn&~nil) (min ?gar1) (max ?max) (com ?com) (layer ?lay) (pin-name ?pn))
  ?v2 <- (vertical (status nil) (net-name ?nn&~nil) (min ?max) (max ?max2) (com ?com) (layer ?lay) (pin-name ?pn1&~?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (pin-name ?pn1))
  =>
  (modify ?v1 (max ?max2))
  (retract ?v2)
)


(defrule p135
  (context (present propagate-constraint))
  ?v1 <-(vertical (status nil) (net-name ?nn&~nil) (min ?min) (max ?gar1) (com ?com) (layer ?lay) (pin-name ?pn))
  ?v2 <- (vertical (status nil) (net-name ?nn&~nil) (min ?min2) (max ?min) (com ?com) (layer ?lay) (pin-name ?pn1&~?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (pin-name ?pn1))
  =>
  (modify ?v1 (min ?min2))
  (retract ?v2)
)

(defrule p136
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~east) (net-name ?nn) (grid-x ?max1) (grid-y ?com1) (grid-layer ?lay) (pin-name ?pn))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?garb1&:(> ?garb1 ?max1)) (com ?com1) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?com1) (layer ?lay)))
  =>
  (modify ?h1 (min =(+ ?max1 1)) (min-net ?nn))
  (assert (horizontal (min ?max1) (max =(+ ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (compo ?cpo) (commo ?cmo)))
  (modify ?ff1 (grid-x =(+ ?max1 1)) (came-from west) (can-chng-layer nil))
)

(defrule p137
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~west) (net-name ?nn) (grid-x ?max1) (grid-y ?com1) (grid-layer ?lay) (pin-name ?pn))
  ?h1 <- (horizontal (net-name nil) (min ?garb1) (max ?max1&:(> ?max1 ?garb1)) (com ?com1) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?max1) (max ?qw86&:(> ?qw86 ?max1)) (com ?com1) (layer ?lay)))
  =>
  (modify ?h1 (max =(- ?max1 1)) (max-net ?nn))
  (assert (horizontal (max ?max1) (min =(- ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (compo ?cpo) (commo ?cmo)))
  (modify ?ff1 (grid-x =(- ?max1 1)) (came-from east) (can-chng-layer nil))
)

(defrule p138
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~north) (net-name ?nn) (grid-x ?com1) (grid-y ?max1) (grid-layer ?lay) (pin-name ?pn))
  ?v1 <-(vertical (net-name nil) (min ?max1) (max ?garb1&:(> ?garb1 ?max1)) (com ?com1) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?com1) (layer ?lay)))
  =>
  (modify ?v1 (min =(+ ?max1 1)) (min-net ?nn))
  (assert (vertical (min ?max1) (max =(+ ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (compo ?cpo) (commo ?cmo)))
  (modify ?ff1 (grid-y =(+ ?max1 1)) (came-from south) (can-chng-layer nil))
)

(defrule p139
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?ff1 <- (ff (can-chng-layer no) (came-from ~south) (net-name ?nn) (grid-x ?com1) (grid-y ?max1) (grid-layer ?lay) (pin-name ?pn))
  ?v1 <-(vertical (net-name nil) (min ?garb1) (max ?max1&:(> ?max1 ?garb1)) (com ?com1) (layer ?lay) (compo ?cpo) (commo ?cmo))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1) (layer ?lay)))
  (not (vertical (net-name nil) (min ?max1) (max ?qw86&:(> ?qw86 ?max1)) (com ?com1) (layer ?lay)))
  =>
  (modify ?v1 (max =(- ?max1 1)) (max-net ?nn))
  (assert (vertical (max ?max1) (min =(- ?max1 1)) (com ?com1) (layer ?lay) (net-name ?nn) (pin-name ?pn) (compo ?cpo) (commo ?cmo)))
  (modify ?ff1 (grid-y =(- ?max1 1)) (came-from north) (can-chng-layer nil))
)

(defrule p140
  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) (com ?com) (layer ?egarb1) (compo ?egarb2) (commo ?egarb3) (pin-name ?pn1))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com)) (max ?qw62&:(>= ?qw62 ?com)) (com ?qw60&:(and (>= ?qw60 ?min) (<= ?qw60 ?max))) (pin-name ?pn2&~?pn1))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  (not (included ?nn ?pn1))
  (included ?nn ?pn2)
  =>
  (assert (included ?nn ?pn1))
  (modify ?t1 (no-of-attached-pins =(+ ?nap 1)))
)

(defrule p141
  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) (com ?com) (layer ?egarb1) (compo ?egarb2) (commo ?egarb3) (pin-name ?pn1))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com)) (max ?qw62&:(>= ?qw62 ?com)) (com ?qw60&:(and (>= ?qw60 ?min) (<= ?qw60 ?max))) (pin-name ?pn2&~?pn1))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  (included ?nn ?pn1)
  (not (included ?nn ?pn2))
  =>
  (assert (included ?nn ?pn2))
  (modify ?t1 (no-of-attached-pins =(+ ?nap 1)))
)

(defrule p142
  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  (vertical (status nil) (net-name ?nn&~nil) (max ?max) (com ?com) (layer ?egarb1) (compo ?egarb2) (commo ?egarb3) (pin-name ?pn1))
  (vertical (status nil) (net-name ?nn&~nil) (min ?max) (com ?com) (pin-name ?pn2&~?pn1))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  (included ?nn ?pn1)
  (not (included ?nn ?pn2))
  =>
  (assert (included ?nn ?pn2))
  (modify ?t1 (no-of-attached-pins =(+ ?nap 1)))
)

(defrule p143
  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  (vertical (status nil) (net-name ?nn&~nil) (min ?egarb4) (max ?max) (com ?com) (layer ?egarb1) (compo ?egarb2) (commo ?egarb3) (pin-name ?pn1))
  (vertical (status nil) (net-name ?nn&~nil) (min ?max) (com ?com) (pin-name ?pn2&~?pn1))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  (not (included ?nn ?pn1))
  (included ?nn ?pn2)
  =>
  (assert (included ?nn ?pn1))
  (modify ?t1 (no-of-attached-pins =(+ ?nap 1)))
)

(defrule p144
  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?egarb4) (max ?max) (com ?com) (layer ?egarb1) (compo ?egarb2) (commo ?egarb3) (pin-name ?pn1))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?max) (com ?com) (pin-name ?pn2&~?pn1))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  (included ?nn ?pn1)
  (not (included ?nn ?pn2))
  =>
  (assert (included ?nn ?pn2))
  (modify ?t1 (no-of-attached-pins =(+ ?nap 1)))
)

(defrule p145
  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  (horizontal (status nil) (net-name ?nn&~nil) (max ?max) (com ?com) (layer ?egarb1) (compo ?egarb2) (commo ?egarb3) (pin-name ?pn1))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?max) (com ?com) (pin-name ?pn2&~?pn1))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  (not (included ?nn ?pn1))
  (included ?nn ?pn2)
  =>
  (assert (included ?nn ?pn1))
  (modify ?t1 (no-of-attached-pins =(+ ?nap 1)))
)

(defrule p146
  ?c1 <-  (context (present propagate-constraint | check-for-routed-net | find-no-of-pins-on-a-row-col))
  ?t1 <-  (to-be-routed (net-name ?nn) (no-of-attached-pins ?nap))
  ?n1 <- (net (net-name ?nn) (net-no-of-pins ?nap))
  =>
  (retract ?c1 ?t1)
  (assert (join-routed-net ?nn))
  (modify ?n1 (net-is-routed yes))
)



(defrule p147
  ?c1 <-  (context (present check-for-routed-net))
  =>
  (retract ?c1)
  (assert (context (present move-ff)))
)

(defrule p148
  ?con <- (context (present move-ff))
  =>
  (modify ?con (present set-min-max))
)

(defrule p149
  ?con <- (context (present set-min-max))
  =>
  (retract ?con)
  (assert (context (present propagate-constraint)))
)

(defrule p150
  (context (present propagate-constraint))
  ?c1 <-  (constraint (pin-name-2 ?pn))
  (not (ff (pin-name ?pn)))
  =>
  (retract ?c1)
)

(defrule p151
  (context (present propagate-constraint))
  ?c1 <-  (constraint (pin-name-1 ?pn))
  (not (ff (pin-name ?pn)))
  =>
  (retract ?c1)
)

(defrule p152
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn))
  (context (present propagate-constraint | extend-total-verti | move-ff))
  ?ff2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?garb2) (pin-name ~?pn))
  =>
  (retract ?ff1 ?ff2)
)

(defrule p153
  (context (present propagate-constraint | extend-pins | find-no-of-pins-on-a-row-col | check-for-routed-net))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (pin-name ?pn))
  ?ff2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2) (grid-layer ?lay) (pin-name ~?pn))
  (congestion (direction row) (coordinate ?gy2) (como ?gy1))
  ?v1 <-(vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1 ?ff2)
  (assert (vertical (min ?min) (max ?gy1) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?v1 (min ?gy2) (min-net ?nn))
  (assert (vertical (min ?gy1) (max ?gy2) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p154
  (context (present propagate-constraint | extend-pins | find-no-of-pins-on-a-row-col | check-for-routed-net))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1&:(<= ?garb1 ?gx)) (max ?garb2&:(>= ?garb2 ?gx)) (com ?gy2) (layer ?lay) (compo ?egarb1) (commo ?gy1) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2)))
  ?v1 <-(vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1)
  (assert (vertical (min ?min) (max ?gy1) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?v1 (min ?gy2) (min-net ?nn))
  (assert (vertical (min ?gy1) (max ?gy2) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p155
  (context (present propagate-constraint | extend-pins | find-no-of-pins-on-a-row-col | check-for-routed-net))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2) (grid-layer ?lay) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1&:(<= ?garb1 ?gx)) (max ?garb2&:(>= ?garb2 ?gx)) (com ?gy1) (layer ?lay) (compo ?gy2) (commo ?egarb1) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1)))
  ?v1 <-(vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1)
  (assert (vertical (min ?min) (max ?gy1) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?v1 (min ?gy2) (min-net ?nn))
  (assert (vertical (min ?gy1) (max ?gy2) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p156
  (context (present propagate-constraint | extend-pins | find-no-of-pins-on-a-row-col | check-for-routed-net))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (horizontal (net-name nil) (min ?min&:(<= ?min ?gx1)) (max ?max&:(and (> ?max ?gx1) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (pin-name ?garb1))
  ?ff2 <- (ff (net-name ?nn) (grid-x ?gx2&:(and (> ?gx2 ?gx1) (<= ?gx2 ?max))) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  (congestion (direction col) (coordinate ?gx2) (como ?gx1))
  ?h1 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx1)) (max ?max&:(and (>= ?max ?gx2) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1 ?ff2)
  (assert (horizontal (min ?min) (max ?gx1) (com ?gy) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?h1 (min ?gx2) (min-net ?nn))
  (assert (horizontal (min ?gx1) (max ?gx2) (com ?gy) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p157
  (context (present propagate-constraint | extend-pins | find-no-of-pins-on-a-row-col | check-for-routed-net))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (vertical (status nil) (net-name ?nn&~nil) (min ?garb1&:(<= ?garb1 ?gy)) (max ?garb2&:(>= ?garb2 ?gy)) (com ?gx2) (layer ?lay) (compo ?egarb1) (commo ?gx1) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-x ?gx2) (grid-y ?gy)))
  ?h1 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx1)) (max ?max&:(and (>= ?max ?gx2) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1)
  (assert (horizontal (min ?min) (max ?gx1) (com ?gy) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?h1 (min ?gx2) (min-net ?nn))
  (assert (horizontal (min ?gx1) (max ?gx2) (com ?gy) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p158
  (context (present propagate-constraint | extend-pins | find-no-of-pins-on-a-row-col | check-for-routed-net))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx2) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (vertical (status nil) (net-name ?nn&~nil) (min ?garb1&:(<= ?garb1 ?gy)) (max ?garb2&:(>= ?garb2 ?gy)) (com ?gx1) (layer ?lay) (compo ?gx2) (commo ?egarb1) (pin-name ~?pn))
  (not (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy)))
  ?h1 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx1)) (max ?max&:(and (>= ?max ?gx2) (> ?max ?min))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1)
  (assert (horizontal (min ?min) (max ?gx1) (com ?gy) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?h1 (min ?gx2) (min-net ?nn))
  (assert (horizontal (min ?gx1) (max ?gx2) (com ?gy) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p159
  (context (present propagate-constraint))
  ?ff1 <- (ff (can-chng-layer ~no) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  (not (vertical (net-name ~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ~?lay)))
  (not (horizontal (net-name ~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy) (layer ~?lay)))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?qz60&:(and (>= ?qz60 ?gy) (> ?qz60 ?vmin))) (com ?gx) (layer ?lay2&~?lay))
  =>
  (modify ?ff1 (grid-layer ?lay2) (can-chng-layer no))
)
