

(defrule p457
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn left ? 1)
  (ff (came-from west) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin&:(> ?hmin ?gx)) (max ?garb1) (com ?hcom&:(< ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  (not (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?hcom)))
  ?c6 <- (horizontal (net-name nil) (min ?hhmin) (max ?hmin&:(> ?hmin ?hhmin)) (com ?hcom) (layer ?lay) (compo ?garb4) (commo ?garb5))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?hcom)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?gy))) (com ?vcom&:(and (>= ?vcom ?hhmin) (< ?vcom ?hmin))))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz49&:(and (>= ?qz49 ?hhmin) (< ?qz49 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?hcom) (< ?qz38 ?gy)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?hmin) (> ?qz61 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c6 (max ?vcom) (max-net ?nn))
  (modify ?c4 (min ?vcom))
  (assert (pull-ff east ?nn ?gx ?gy ?gy ?vcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from east)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p458
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn left ? 1)
  (ff (came-from west) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (net-name ?nn&~nil) (min ?hmin&:(> ?hmin ?gx)) (max ?garb1) (com ?hcom&:(< ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?hcom) (grid-layer ?garb4) (pin-name ?garb5))
  ?c7 <- (horizontal (net-name nil) (min ?hhmin) (max ?hmin&:(> ?hmin ?hhmin)) (com ?hcom) (layer ?lay) (compo ?garb6) (commo ?garb7))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?hcom)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?gy))) (com ?vcom&:(and (>= ?vcom ?hhmin) (< ?vcom ?hmin))))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz49&:(and (>= ?qz49 ?hhmin) (< ?qz49 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?hcom) (< ?qz38 ?gy)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?hmin) (> ?qz61 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c7 (max ?vcom) (max-net ?nn))
  (modify ?c4 (min ?vcom))
  (assert (pull-ff east ?nn ?gx ?gy ?gy ?vcom))
  (modify ?c5 (grid-x ?vcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p459
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn left ? 1)
  (ff (came-from west) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (net-name ?nn&~nil) (min ?hmin&:(> ?hmin ?gx)) (max ?garb1) (com ?hcom&:(> ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  (not (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?hcom)))
  ?c6 <- (horizontal (net-name nil) (max ?hmin) (min ?hhmin&:(< ?hhmin ?hmin)) (com ?hcom) (layer ?lay) (compo ?garb4) (commo ?garb5))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?hcom))) (com ?vcom&:(and (>= ?vcom ?hhmin) (< ?vcom ?hmin))))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?qz49&:(and (>= ?qz49 ?hhmin) (< ?qz49 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?hmin) (> ?qz61 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c6 (max ?vcom) (max-net ?nn))
  (modify ?c4 (min ?vcom))
  (assert (pull-ff east ?nn ?gx ?gy ?gy ?vcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from east)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p460
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn left ? 1)
  (ff (came-from west) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin&:(> ?hmin ?gx)) (max ?garb1) (com ?hcom&:(> ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?hcom) (grid-layer ?garb4) (pin-name ?garb5))
  ?c7 <- (horizontal (net-name nil) (min ?hhmin) (max ?hmin&:(> ?hmin ?hhmin)) (com ?hcom) (layer ?lay) (compo ?garb6) (commo ?garb7))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?hcom))) (com ?vcom&:(and (>= ?vcom ?hhmin) (< ?vcom ?hmin))))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?qz49&:(and (>= ?qz49 ?hhmin) (< ?qz49 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz61&:(and (< ?qz61 ?hmin) (> ?qz61 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c7 (max ?vcom) (max-net ?nn))
  (modify ?c4 (min ?vcom))
  (assert (pull-ff east ?nn ?gx ?gy ?gy ?vcom))
  (modify ?c5 (grid-x ?vcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p461
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn right ? 1)
  (ff (came-from east) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?hmax&:(< ?hmax ?gx)) (com ?hcom&:(> ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  (not (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?hcom)))
  ?c6 <- (horizontal (net-name nil) (min ?hmax) (max ?hhmax&:(> ?hhmax ?hmax)) (com ?hcom) (layer ?lay) (compo ?garb4) (commo ?garb5))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?hcom))) (com ?vcom&:(and (<= ?vcom ?hhmax) (> ?vcom ?hmax))))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?qz53&:(and (<= ?qz53 ?hhmax) (> ?qz53 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?hmax) (< ?qz39 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c6 (min ?vcom) (min-net ?nn))
  (modify ?c4 (max ?vcom))
  (assert (pull-ff west ?nn ?gx ?gy ?gy ?vcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from west)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p462
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn right ? 1)
  (ff (came-from east) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?hmax&:(< ?hmax ?gx)) (com ?hcom&:(> ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?hcom) (grid-layer ?garb4) (pin-name ?garb5))
  ?c7 <- (horizontal (net-name nil) (min ?hmax) (max ?hhmax&:(> ?hhmax ?hmax)) (com ?hcom) (layer ?lay) (compo ?gabr6) (commo ?garb7))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?hcom))) (com ?vcom&:(and (<= ?vcom ?hhmax) (> ?vcom ?hmax))))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?qz53&:(and (<= ?qz53 ?hhmax) (> ?qz53 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?gy) (< ?qz38 ?hcom)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?hmax) (< ?qz39 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c7 (min ?vcom) (min-net ?nn))
  (modify ?c4 (max ?vcom))
  (assert (pull-ff west ?nn ?gx ?gy ?gy ?vcom))
  (modify ?c5 (grid-x ?vcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)



(defrule p463
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn right ? 1)
  (ff (came-from east) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?hmax&:(< ?hmax ?gx)) (com ?hcom&:(< ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  (not (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?hcom)))
  ?c6 <- (horizontal (net-name nil) (min ?hmax) (max ?hhmax&:(> ?hhmax ?hmax)) (com ?hcom) (layer ?lay) (compo ?garb4) (commo ?gabr5))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?hcom)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?gy))) (com ?vcom&:(and (<= ?vcom ?hhmax) (> ?vcom ?hmax))))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qz53&:(and (<= ?qz53 ?hhmax) (> ?qz53 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?hcom) (< ?qz38 ?gy)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?hmax) (< ?qz39 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c6 (min ?vcom) (min-net ?nn))
  (modify ?c4 (max ?vcom))
  (assert (pull-ff west ?nn ?gx ?gy ?gy ?vcom))
  (assert (ff (net-name ?nn) (pin-name ?pn) (grid-layer ?lay) (grid-x ?vcom) (grid-y ?hcom) (came-from west)))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p464
  ?f1 <- (context (present extend-total-verti))
  ?c2 <- (total-verti ?nn right ? 1)
  (ff (came-from east) (net-name ?nn) (grid-x ?gx) (grid-y ?gy))
  ?c4 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?hmax&:(< ?hmax ?gx)) (com ?hcom&:(< ?hcom ?gy)) (layer ?lay) (compo ?garb2) (commo ?garb3) (pin-name ?pn))
  (not (horizontal-s (net-name ~?nn) (com ?hcom)))
  ?c5 <- (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?hcom) (grid-layer ?garb4) (pin-name ?garb5))
  ?c7 <- (horizontal (net-name nil) (min ?hmax) (max ?hhmax&:(> ?hhmax ?hmax)) (com ?hcom) (layer ?lay) (compo ?garb6) (commo ?garb7))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?hcom)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?gy))) (com ?vcom&:(and (<= ?vcom ?hhmax) (> ?vcom ?hmax))))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?qz53&:(and (<= ?qz53 ?hhmax) (> ?qz53 ?vcom)))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (com ?qz38&:(and (> ?qz38 ?hcom) (< ?qz38 ?gy)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (com ?qz39&:(and (> ?qz39 ?hmax) (< ?qz39 ?vcom)))))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?vcom) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw76&:(<= ?qw76 ?vcom)) (max ?qw65&:(>= ?qw65 ?vcom)) (com ?hcom) (layer ?lay)))
  =>
  (modify ?c7 (min ?vcom) (min-net ?nn))
  (modify ?c4 (max ?vcom))
  (assert (pull-ff west ?nn ?gx ?gy ?gy ?vcom))
  (modify ?c5 (grid-x ?vcom) (grid-layer ?lay))
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p465
  ?c1 <-  (context (present remove-total-verti-gt1))
  =>
  (modify ?c1 (present find-no-of-pins-on-a-row-col))
)

(defrule p466
  ?c1 <-  (context (present extend-total-verti))
  =>
  (modify ?c1 (present remove-total-verti-gt1))
)

(defrule p467
  (context (present remove-total-verti-gt1))
  (total-verti ? ? ? ?qw86&:(> ?qw86 1))
  ?t <- (total-verti ? ? ? 1)
  =>
  (retract ?t)
)

(defrule p468
  ?c1 <-  (context (present remove-total-verti-gt1))
  (total-verti ? ? ? ?qw86&:(> ?qw86 1))
  (not (total-verti ? ? ? 1))
  =>
  (assert (remove-all-total-vertis))
  (modify ?c1 (present extend-total-verti))
)

(defrule p469
  ?c1 <-  (context (present remove-total-verti-gt1))
  ?c <- (retract-all-total-vertis)
  (not (total-verti ? ? ? ?))
  =>
  (retract ?c)
  (modify ?c1 (present propagate-constraint))
)

(defrule p470
  ?c1 <-  (context (present remove-total-verti-gt1))
  ?c <- (retract-all-total-vertis)
  =>
  (retract ?c)
  (modify ?c1 (present find-no-of-pins-on-a-row-col))
)

(defrule p471
  ?c1 <-  (context (present extend-total-verti | remove-total-verti-gt1))
  (not (total-verti))
  =>
  (modify ?c1 (present propagate-constraint))
)

(defrule p515
  ?c <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  ?p <- (pull-ff north ?nn ?gx ?gy1 ?gx ?gy2)
  ?ff <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (came-from south) (pin-name ?pn))
  ?v <- (vertical (net-name nil) (min ?gy1) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx)
                  (layer ?lay) (compo ?xc1) (commo ?xc2) (status ?xc3)
                  (min-net ?xc4) (max-net ?xc5))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy2)) (com ?gx) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2) (layer ?lay)))
  =>
  (retract ?p ?c)
  (assert (vertical (net-name ?nn) (pin-name ?pn) (max ?gy2)
                      (min ?gy1) (com ?gx) (compo ?xc1) (commo ?xc2) (layer ?lay) 
                      (status ?xc3) (min-net ?xc4) (max-net ?xc5)))
  (modify ?v (min ?gy2) (min-net ?nn))
  (modify ?ff (grid-y ?gy2) (can-chng-layer nil))
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p516
  ?c <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  ?p <- (pull-ff south ?nn ?gx ?gy1 ?gx ?gy2)
  ?ff <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (came-from north) (pin-name ?pn))
  ?v <- (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?gy1) (com ?gx) 
                  (layer ?lay) (compo ?xc1) (commo ?xc2) (status ?xc3)
                    (min-net ?xc4) (max-net ?xc5))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy2)) (com ?gx) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2) (layer ?lay)))
  =>
  (retract ?p ?c)
  (assert (vertical (net-name ?nn) (pin-name ?pn) (min ?gy2)
                      (max ?gy1) (com ?gx) (compo ?xc1) (commo ?xc2) (layer ?lay) 
                      (status ?xc3) (min-net ?xc4) (max-net ?xc5)))
  (modify ?v (max ?gy2) (max-net ?nn))
  (modify ?ff (grid-y ?gy2) (can-chng-layer nil))
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p517
  ?c <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  ?p <- (pull-ff east ?nn ?gx1 ?gy ?gx2 ?gy)
  ?ff <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (came-from west) (pin-name ?pn))
  ?v <- (horizontal (net-name nil) (min ?gx1) (max ?qw62&:(>= ?qw62 ?gx2)) (com ?gy) 
                   (layer ?lay) (compo ?xc1) (commo ?xc2) (status ?xc3)
                    (min-net ?xc4) (max-net ?xc5))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx2) (layer ?lay)))
  =>
  (retract ?p ?c)
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (max ?gx2)
                      (min ?gx1) (com ?gy) (compo ?xc1) (commo ?xc2) (layer ?lay) 
                      (status ?xc3) (min-net ?xc4) (max-net ?xc5)))
  (modify ?v (min ?gx2) (min-net ?nn))
  (modify ?ff (grid-x ?gx2) (can-chng-layer nil))
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p518
  ?c <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  ?p <- (pull-ff west ?nn ?gx1 ?gy ?gx2 ?gy)
  ?ff <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (came-from east) (pin-name ?pn))
  ?v <- (horizontal (net-name nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?gx1) (com ?gy) 
                    (layer ?lay) (compo ?xc1) (commo ?xc2) (status ?xc3)
                    (min-net ?xc4) (max-net ?xc5))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw38&:(<= ?qw38 ?gx2)) (max ?qw62&:(>= ?qw62 ?gx2)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx2) (layer ?lay)))
  =>
  (retract ?p ?c)
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (min ?gx2)
                      (max ?gx1) (com ?gy) (compo ?xc1) (commo ?xc2) (layer ?lay) 
                      (status ?xc3) (min-net ?xc4) (max-net ?xc5)))
  (modify ?v (max ?gx2) (max-net ?nn))
  (modify ?ff (grid-x ?gx2) (can-chng-layer nil))
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p293
  (eliminate-total)
  (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (min-xy ?min1) (max-xy ?max1) (level-pins ?p1))
  ?t1 <-  (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (min-xy ?min2) (max-xy ?max2&:(> ?max2 ?max1)) (level-pins ?qz20&:(< ?qz20 ?p1)))
  (not (total (row-col ?rc) (coor ?xy) (max-xy ?qz21&:(< ?qz21 ?min2))))
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qz38&:(and (> ?qz38 ?max1) (< ?qz38 ?max2)))))
  =>
  (retract ?t1)
)

(defrule p294
  (eliminate-total)
  (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (min-xy ?min1) (max-xy ?max1) (level-pins ?p1))
  ?t1 <-  (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (min-xy ?min2) (max-xy ?max2&:(< ?max2 ?max1)) (level-pins ?qz21&:(< ?qz21 ?p1)))
  (not (total (row-col ?rc) (coor ?xy) (max-xy ?qz20&:(< ?qz20 ?min2))))
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qz38&:(and (> ?qz38 ?max2) (< ?qz38 ?max1)))))
  =>
  (retract ?t1)
)



(defrule p295
  (eliminate-total)
  (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (min-xy ?min1) (max-xy ?max1) (level-pins ?p1))
  ?t1 <-  (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (min-xy ?min2&:(< ?min2 ?min1)) (max-xy ?max2) (level-pins ?qz22&:(< ?qz22 ?p1)))
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?max2))))
  (not (total (row-col ?rc) (coor ?xy) (max-xy ?qz38&:(and (> ?qz38 ?min2) (< ?qz38 ?min1)))))
  =>
  (retract ?t1)
)

(defrule p296
  (eliminate-total)
  (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (min-xy ?min1) (max-xy ?max1) (level-pins ?p1))
  ?t1 <-  (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (min-xy ?min2&:(> ?min2 ?min1)) (max-xy ?max2) (level-pins ?qz22&:(< ?qz22 ?p1)))
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?max2))))
  (not (total (row-col ?rc) (coor ?xy) (max-xy ?qz38&:(and (> ?qz38 ?min1) (< ?qz38 ?min2)))))
  =>
  (retract ?t1)
)

(defrule p297
  (context (present merge))
  ?m <- (merge-direction left)
  ?t1 <-  (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (level-pins ?lcou) 
                 (total-pins ?tcou) (min-xy ?min) (max-xy ?max) (nets $?n1))
  (not (rmerge $?))
  (not (total (net-name ~?nn1) (row-col ?rc) (coor ?xy) (max-xy ?qz20&:(< ?qz20 ?max))))
  (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?max)))
  =>
  (retract ?m)
  (assert (merge-direction right))
  (assert (lmerge ?nn1 ?rc ?xy ?min ?max ?lcou ?tcou $?n1))
)

(defrule p298
  (context (present merge))
  ?m <- (merge-direction right)
  ?t1 <-  (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (level-pins ?lcou) 
                 (total-pins ?tcou) (min-xy ?min) (max-xy ?max) (nets $?n1))
  (not (lmerge $?))
  (not (total (net-name ~?nn1) (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?min))))
  (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (max-xy ?qz20&:(< ?qz20 ?min)))
  =>
  (retract ?m)
  (assert (merge-direction left))
  (assert (rmerge ?nn1 ?rc ?xy ?min ?max ?lcou ?tcou $?n1))
)

(defrule p299
  ?c <- (lmerge ?nn1 ?rc ?xy ?min ?max ?lcou ?tcou $?rest)
  ?t2 <- (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (level-pins ?cou3) 
                (total-pins ?cou4) (min-xy ?min1&:(> ?min1 ?max)) (max-xy ?max1) (nets $?n1))
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?max)) 
              (max-xy ?qz20&:(< ?qz20 ?min1))))
  (not (place-holder ?rc ?xy > ?max < ?min1))
  =>
  (assert (total (net-name ?nn1) (row-col ?rc) (coor ?xy) (level-pins =(+ ?lcou ?cou3)) 
                 (total-pins =(+ ?tcou ?cou4)) (min-xy ?min) (max-xy ?max1) 
                 (nets $?rest $?n1)))
  (assert (delete-merged ?nn2 ?rc ?xy ?min1 ?max1 =(gensym)))
)

(defrule p300
  ?c <- (rmerge ?nn1 ?rc ?xy ?min ?max ?lcou ?tcou $?rest)
  ?t2 <- (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (level-pins ?cou3) 
                (total-pins ?cou4) (min-xy ?min1) (max-xy ?max1&:(< ?max1 ?min)) (nets $?n1))
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?max1)) (max-xy ?qz20&:(< ?qz20 ?min))))
  (not (place-holder ?rc ?xy > ?max1 < ?min))
  =>
  (assert (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (level-pins =(+ ?lcou ?cou3))
                (total-pins =(+ ?tcou ?cou4)) (min-xy ?min1) (max-xy ?max) 
                (nets $?rest $?n1)))
  (assert (rdelete-merged ?nn2 ?rc ?xy ?min1 ?max1 =(gensym)))
)

(defrule p301
  ?d <- (delete-merged ?nn ?rc ?xy ?min ?max ?id)
  (delete-merged ?nn ?rc ?xy ?min ?max ~?id)
  =>
  (retract ?d)
)

(defrule p302
  ?d <- (rdelete-merged ?nn ?rc ?xy ?min ?max ?id)
  (rdelete-merged ?nn ?rc ?xy ?min ?max ~?id)
  =>
  (retract ?d)
)

(defrule p303
  (delete-merged ?nn ?rc ?xy ?min ?max)
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?xy) (min-xy ?min) (max-xy ?max))
  (not (lmerge $?))
  =>
  (retract ?t)
)

(defrule p304
  (rdelete-merged ?nn ?rc ?xy ?min ?max)
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?xy) (min-xy ?min) (max-xy ?max))
  (not (rmerge $?))
  =>
  (retract ?t)
)

(defrule p305
  ?d <- (delete-merged | rdelete-merged)
  (not (lmerge | rmerge $?))
  =>
  (retract ?d)
)

(defrule p306
  (delete-merged ?nn ?rc ?xy ?min ?max)
  ?t1 <-  (total (net-name ?nn) (row-col ?rc) (coor ?xy) (min-xy ?min) (max-xy ?max) 
                 (level-pins ?cou1) (total-pins ?cou2) (nets $?n1))
  ?t2 <- (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (level-pins ?cou3) 
                (total-pins ?cou4) (min-xy ?min1) (max-xy ?max1&:(< ?max1 ?min)) (nets $?n2))
  (not (lmerge $?))
  =>
  (assert (place-holder ?rc ?xy ?min ?max =(gensym)))
  (assert (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (level-pins =(+ ?cou1 ?cou3)) 
                 (total-pins =(+ ?cou2 ?cou4)) (min-xy ?min1) (max-xy ?max) 
                 (nets $?n1 $?n2)))
)


(defrule p307
  (rdelete-merged ?nn ?rc ?xy ?min ?max)
  ?t1 <-  (total (net-name ?nn) (row-col ?rc) (coor ?xy) (min-xy ?min) (max-xy ?max) 
                 (level-pins ?cou1) (total-pins ?cou2) (nets $?n1))
  ?t2 <- (total (net-name ?nn2) (row-col ?rc) (coor ?xy) (level-pins ?cou3) 
                (total-pins ?cou4) (min-xy ?min1&:(> ?min1 ?max)) (max-xy ?max1) (nets $?n2))
  (not (rmerge $?))
  =>
  (assert (place-holder ?rc ?xy ?min ?max =(gensym)))
  (assert (total (net-name ?nn) (row-col ?rc) (coor ?xy) (level-pins =(+ ?cou1 ?cou3)) 
                 (total-pins =(+ ?cou2 ?cou4)) (min-xy ?min) (max-xy ?max1)
                 (nets $?n1 $?n2)))
)

(defrule p308
  ?p <- (place-holder ?rc ?xy ?min ?max ?id)
  (place-holder ?rc ?xy ?min ?max ~?id)
  =>
  (retract ?p)
)

(defrule p309
  (lmerge | rmerge $?)
  ?p <- (place-holder ?rc ?xy ?min ?max ?id)
  (not (total (row-col ?rc) (coor ?xy) (max-xy ?qz20&:(< ?qz20 ?min))))
  =>
  (retract ?p)
)

(defrule p310
  (lmerge | rmerge $?)
  ?p <- (place-holder ?rc ?xy ?min ?max ?id)
  (not (total (row-col ?rc) (coor ?xy) (min-xy ?qw86&:(> ?qw86 ?max))))
  =>
  (retract ?p)
)

(defrule p311
  (context (present merge))
  ?l <- (lmerge ?nn ?rc ?xy ?min ?max $?)
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?xy) (min-xy ?min) (max-xy ?max))
  =>
  (retract ?l ?t)
)

(defrule p312
  (context (present merge))
  ?l <- (rmerge ?nn ?rc ?xy ?min ?max $?)
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?xy) (min-xy ?min) (max-xy ?max))
  =>
  (retract ?l ?t)
)

(defrule p313
  (context (present merge))
  (total (level-pins ?cou) (total-pins ?cou))
  (not (total (level-pins ?cou1&:(> ?cou1 ?cou)) (total-pins ?cou1)))
  ?m <- (maximum-total ?mt1&:(< ?mt1 ?cou) $?rest)
  =>
  (retract ?m)
  (assert (maximum-total ?cou $?rest))
)

(defrule p314
  (context (present merge))
  (total (row-col row) (coor 1) (level-pins ?cou) (total-pins ?cou))
  (not (total (row-col row) (coor 1) (level-pins ?cou1&:(> ?cou1 ?cou)) 
              (total-pins ?cou1)))
  ?m <- (maximum-total ?mt1 ?mt2 ?mt3 1 ?mt4&:(< ?mt4 ?cou) $?rest)
  =>
  (retract ?m)
  (assert (maximum-total ?mt1 ?mt2 ?mt3 1 ?cou $?rest))
)

(defrule p315
  (context (present merge))
  (last-row ?lr)
  (total (row-col row) (coor ?lr) (level-pins ?cou) (total-pins ?cou))
  (not (total (row-col row) (coor ?lr) (level-pins ?cou1&:(> ?cou1 ?cou)) (total-pins ?cou1)))
  ?m <- (maximum-total ?mt1 ?mt2 ?mt3 ?mt4 ?mt5 ?lr ?mt6&:(< ?mt6 ?cou) $?rest)
  =>
  (retract ?m)
  (assert (maximum-total ?mt1 ?mt2 ?mt3 ?mt4 ?mt5 ?lr ?cou $?rest))
)

(defrule p316
  (context (present merge))
  (total (level-pins ?cou1) (total-pins ?cou2))
  (not (total (level-pins ?qw86&:(> ?qw86 ?cou1)) (total-pins ?qw87&:(> ?qw87 ?cou2))))
  ?m <- (maximum-total ?garb4 ?min1&:(< ?min1 ?cou1) ? $?rest)
  =>
  (retract ?m)
  (assert (maximum-total ?garb4 ?cou1 ?cou2 $?rest))
)



(defrule p317
  ?con <- (context (present merge))
  =>
  (modify ?con (present delete-totals))
  (assert (goal cleanup eliminate-total))
  (assert (goal cleanup merge-direction))
)

(defrule p318
  (context (present delete-totals))
  (maximum-total ?max ?garb1 ?garb2 $?)
  ?t <- (total (level-pins ?cou&:(< ?cou ?max)) (total-pins ~?cou))
  =>
  (retract ?t)
)

(defrule p319
  (context (present delete-totals))
  (maximum-total ? ? ? 1 ?cou $?rest)
  ?t <- (total (row-col row) (coor 1) (level-pins ?cou1&:(< ?cou1 ?cou)) (total-pins ?cou1))
  =>
  (retract ?t)
)

(defrule p320
  (context (present delete-totals))
  (maximum-total ? ? ? ? ? ?lr ?cou $?rest)
  ?t <- (total (row-col row) (coor ?lr) (level-pins ?cou1&:(< ?cou1 ?cou)) (total-pins ?cou1))
  =>
  (retract ?t)
)



(defrule p321
  (context (present delete-totals))
  (maximum-total ?garb1 ?min ?max $?rest)
  ?t <- (total (level-pins ?cou&:(< ?cou ?min)))
  (last-row ?lr)
  (total (row-col row) (coor ~1&~?lr))
  =>
  (retract ?t)
)

(defrule p322
  (context (present delete-totals))
  (total (row-col ?rc) (coor ?xy) (level-pins ?min) (total-pins ?max))
  ?t1 <-  (total (row-col ?rc) (coor ?xy) (level-pins ?cou&:(<= ?cou ?min)) (total-pins ?qw86&:(> ?qw86 ?max)))
  =>
  (retract ?t1)
)

(defrule p323
  (context (present delete-totals))
  ?m <- (maximum-total ?garb1 ?garb2 ?garb3 $?)
  =>
  (retract ?m)
)

(defrule p324
  ?c1 <- (context (present delete-totals))
  (not (maximum-total $?))
  ?t1 <-  (total (net-name ?nn1) (row-col row) (coor 1) (nets $?n1))
  ?t2 <- (total (net-name ?nn2) (row-col row) (coor ?lr) (nets $?n2))
  (not (total (row-col row) (coor ~1&~?lr)))
  (last-row ?lr)
  =>
  (retract ?c1 ?t1 ?t2)
  (assert (change-priority))
  (assert (tran-total row 1 $?n1 end))
  (assert (tran-total row ?lr $?n2 end))
  (assert (goal cleanup total))
)
   
(defrule p325
  (context (present delete-totals))
  (not (maximum-total $?))
  (total (net-name ?nn1) (row-col row) (coor 1))
  (total (net-name ?nn2) (row-col row) (coor ?lr))
  ?t1 <-  (total (net-name ~?nn1) (row-col row) (coor 1))
  (last-row ?lr)
  =>
  (retract ?t1)
)
