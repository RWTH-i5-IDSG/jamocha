

(defrule p214
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~north) (can-chng-layer no))
  (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?gy)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy)) (max ?max&:(and (> ?max ?min) (>= ?max ?gy2))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw87&:(> ?qw87 ?gy))))
  (not (vertical (net-name nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw88&:(> ?qw88 ?gy)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?qz20&:(< ?qz20 ?gx)) (grid-y ?qw74&:(<= ?qw74 ?gy))))
  (not (ff (net-name ?nn) (grid-x ?qw89&:(> ?qw89 ?gx)) (grid-y ?qw75&:(<= ?qw75 ?gy))))
  =>
  (assert (vertical (max ?max) (min =(+ ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max ?gy) (max-net ?nn))
  (assert (vertical (min ?gy) (max =(+ ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-y =(+ ?gy 1)) (came-from south) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p215
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~west) (can-chng-layer no))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx)) (grid-y ?qw74&:(<= ?qw74 ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx2)) (max ?max&:(and (> ?max ?min) (>= ?max ?gx))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw62&:(>= ?qw62 ?gx))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-y ?qw86&:(> ?qw86 ?gy))))
  =>
  (assert (horizontal (min ?min) (max =(- ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min ?gx) (min-net ?nn))
  (assert (horizontal (max ?gx) (min =(- ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-x =(- ?gx 1)) (came-from east) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p216
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~west) (can-chng-layer no))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx)) (grid-y ?qw63&:(>= ?qw63 ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx2)) (max ?max&:(and (> ?max ?min) (>= ?max ?gx))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw62&:(>= ?qw62 ?gx))))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy)) (com ?gx) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-y ?qz21&:(< ?qz21 ?gy))))
  =>
  (assert (horizontal (min ?min) (max =(- ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min ?gx) (min-net ?nn))
  (assert (horizontal (max ?gx) (min =(- ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-x =(- ?gx 1)) (came-from east) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p217
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~east) (can-chng-layer no))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx)) (grid-y ?qw74&:(<= ?qw74 ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx)) (max ?max&:(and (> ?max ?min) (>= ?max ?gx2))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw86&:(> ?qw86 ?gx))))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-y ?qw87&:(> ?qw87 ?gy))))
  =>
  (assert (horizontal (max ?max) (min =(+ ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max ?gx) (max-net ?nn))
  (assert (horizontal (min ?gx) (max =(+ ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-x =(+ ?gx 1)) (came-from west) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p218
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~east) (can-chng-layer no))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx)) (grid-y ?qw62&:(>= ?qw62 ?gy)))
  ?f4 <- (horizontal (net-name nil) (min ?min&:(<= ?min ?gx)) (max ?max&:(and (> ?max ?min) (>= ?max ?gx2))) (com ?gy) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw86&:(> ?qw86 ?gx))))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw87&:(> ?qw87 ?gy)) (com ?gx) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-y ?qz20&:(< ?qz20 ?gy))))
  =>
  (assert (horizontal (max ?max) (min =(+ ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max ?gx) (max-net ?nn))
  (assert (horizontal (min ?gx) (max =(+ ?gx 1)) (com ?gy) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-x =(+ ?gx 1)) (came-from west) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p219
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~north) (can-chng-layer no))
  (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?gy)) (grid-x ?qw62&:(>= ?qw62 ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy)) (max ?max&:(and (> ?max ?min) (>= ?max ?gy2))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?vnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?gy) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-x ?qz20&:(< ?qz20 ?gx))))
  =>
  (assert (vertical (max ?max) (min =(+ ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?vnn) (min-net ?nn)))
  (modify ?f4 (max ?gy) (max-net ?nn))
  (assert (vertical (min ?gy) (max =(+ ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-y =(+ ?gy 1)) (came-from south) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p220
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~north) (can-chng-layer no))
  (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?gy)) (grid-x ?qw74&:(<= ?qw74 ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy)) (max ?max&:(and (> ?max ?min) (>= ?max ?gy2))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?vnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw86&:(> ?qw86 ?gy))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-x ?qw87&:(> ?qw87 ?gx))))
  =>
  (assert (vertical (max ?max) (min =(+ ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?vnn) (min-net ?nn)))
  (modify ?f4 (max ?gy) (max-net ?nn))
  (assert (vertical (min ?gy) (max =(+ ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-y =(+ ?gy 1)) (came-from south) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p221
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~south) (can-chng-layer no))
  (ff (net-name ?nn) (grid-y ?gy2&:(< ?gy2 ?gy)) (grid-x ?qw74&:(<= ?qw74 ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy2)) (max ?max&:(and (> ?max ?min) (>= ?max ?gy))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?vnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw62&:(>= ?qw62 ?gy))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-x ?qw86&:(> ?qw86 ?gx))))
  =>
  (assert (vertical (min ?min) (max =(- ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?vnn) (min-net ?nn)))
  (modify ?f4 (min ?gy) (min-net ?nn))
  (assert (vertical (max ?gy) (min =(- ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-y =(- ?gy 1)) (came-from north) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p222
  ?f1 <- (context (present loose-constraint))
  ?f2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn) (came-from ~north) (can-chng-layer no))
  (ff (net-name ?nn) (grid-y ?gy2&:(< ?gy2 ?gy)) (grid-x ?qw62&:(>= ?qw62 ?gx)))
  ?f4 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy2)) (max ?max&:(and (> ?max ?min) (>= ?max ?gy))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?vnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw63&:(>= ?qw63 ?gy))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?gy) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-x ?qz21&:(< ?qz21 ?gx))))
  =>
  (assert (vertical (min ?min) (max =(- ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?vnn) (min-net ?nn)))
  (modify ?f4 (min ?gy) (min-net ?nn))
  (assert (vertical (max ?gy) (min =(- ?gy 1)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (net-name ?nn) (pin-name ?pn)))
  (modify ?f2 (grid-y =(- ?gy 1)) (came-from north) (can-chng-layer nil))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p223
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gx2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw86&:(> ?qw86 ?com1))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw87&:(> ?qw87 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  =>
  (assert (horizontal (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (horizontal (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(+ ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from west)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p224
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?com1)) (grid-x ?gx))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gy2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw87&:(> ?qw87 ?com1))))
  (not (vertical (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw89&:(> ?qw89 ?com1)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  =>
  (assert (vertical (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (vertical (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(+ ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from south)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p225
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?gx2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (max-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw63&:(>= ?qw63 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  =>
  (assert (horizontal (max ?max2) (min ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (horizontal (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(- ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from east)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p226
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-y ?gy2&:(< ?gy2 ?com1)) (grid-x ?gx))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?gy2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (max-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw63&:(>= ?qw63 ?com1))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw65&:(>= ?qw65 ?com1)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  =>
  (assert (vertical (max ?max2) (min ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (vertical (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(- ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from north)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p227
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gx2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw86&:(> ?qw86 ?com1))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw87&:(> ?qw87 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  =>
  (assert (horizontal (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (horizontal (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(+ ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from west)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p228
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-y ?gy2&:(> ?gy2 ?com1)) (grid-x ?gx))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gy2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw86&:(> ?qw86 ?com1))))
  (not (vertical (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw87&:(> ?qw87 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  =>
  (assert (vertical (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (vertical (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(+ ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from south)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p229
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?gx2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (max-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw63&:(>= ?qw63 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  =>
  (assert (horizontal (max ?max2) (min ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (horizontal (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(- ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from east)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p230
  ?f1 <- (context (present loose-constraint))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy2&:(< ?gy2 ?com1)))
  ?f4 <- (vertical (net-name nil) (min ?min2&:(<= ?min2 ?gy2)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?com1))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gx))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (max-net ?hnn))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw63&:(>= ?qw63 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (layer ?lay)))
  (not (ff (net-name ?nn) (grid-y ?com1)))
  =>
  (assert (vertical (max ?max2) (min ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (max-net ?hnn) (min-net ?nn)))
  (modify ?f4 (max =(- ?com1 1)) (max-net ?nn))
  (assert (vertical (max ?com1) (min =(- ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-y =(- ?com1 1)) (grid-x ?com2) (grid-layer ?lay) (came-from north)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p231
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gx2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (>= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw86&:(> ?qw86 ?com1))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw87&:(> ?qw87 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1))) (layer ?lay)))
  (not (vertical (net-name nil) (max ?min1) (com ?com1)))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  (not (vertical (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (horizontal (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (horizontal (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(+ ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from west)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)

(defrule p232
  ?f1 <- (context (present loose-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay) (compo ?garb1) (commo ?garb2) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?com1)) (grid-y ?gy))
  ?f4 <- (horizontal (net-name nil) (min ?min2&:(<= ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?min2) (>= ?max2 ?gx2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1) (<= ?com2 ?gy))) (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?hnn))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw87&:(> ?qw87 ?com1))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1))) (layer ?lay)))
  (not (vertical (net-name nil) (max ?min1) (com ?com1)))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?com1)) (max ?qw89&:(> ?qw89 ?com1)) (com ?com2) (layer ~?lay)))
  (not (ff (net-name ?nn) (grid-x ?com1)))
  (not (vertical (status nil) (net-name ?nn&~nil) (max ?min1) (com ?com1) (layer ?lay)))
  =>
  (assert (horizontal (min ?min2) (max ?com1) (commo ?cmo2) (compo ?cpo2) (layer ?lay) (com ?com2) (min-net ?hnn) (max-net ?nn)))
  (modify ?f4 (min =(+ ?com1 1)) (min-net ?nn))
  (assert (horizontal (min ?com1) (max =(+ ?com1 1)) (com ?com2) (layer ?lay) (net-name ?nn) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-x =(+ ?com1 1)) (grid-y ?com2) (grid-layer ?lay) (came-from west)))
  (retract ?f1)
  (assert (context (present propagate-constraint)))
)
