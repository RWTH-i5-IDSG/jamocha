
(defrule p86
  (context (present propagate-constraint | extend-pins))
  (ff (net-name ?nn1) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  (horizontal (status nil) (net-name ?nn2&~?nn1&~nil) 
              (min ?garb1&:(<= ?garb1 ?gx)) (max ?garb2&:(>= ?garb2 ?gx)) 
              (com ?com) (layer ?lay) (compo ?gy) (commo ?egarb2))
  ?v1 <- (vertical (net-name nil) (min ?min3&:(<= ?min3 ?com)) 
                   (max ?max3&:(and (>= ?max3 ?gy) (> ?max3 ?min3))) (com ?gx) (layer ?lay) 
                   (compo ?cpo2) (commo ?cmo2) (min-net ?mnn) (max-net ?man))
  =>
  (assert (vertical (min ?min3) (max ?com) (com ?gx) (compo ?cpo2) (commo ?cmo2) 
                    (layer ?lay) (max-net ?nn2) (min-net ?mnn)))
  (assert (vertical (min ?gy) (max ?max3) (com ?gx) (compo ?cpo2) (commo ?cmo2) 
                    (layer ?lay) (min-net ?nn1) (max-net ?man)))
  (retract ?v1)
)

(defrule p87
  (context (present propagate-constraint))
  (horizontal (status nil) (net-name ?nn1&~nil) (min ?garb1) (max ?max1) (com ?com) 
                (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (horizontal (status nil) (net-name ?nn2&~?nn1&~nil) (min ?min2) (max ?gar2) 
                (com ?com) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?min2&:(> ?min2 ?max1)) (com ?com) 
                                     (layer ?lay) (compo ?egarb5) (commo ?egarb6))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?max1) (< ?qz38 ?min2)))))
  =>
  (retract ?h1)
)


(defrule p88
  (context (present propagate-constraint | extend-pins | move-ff))
  (vertical (status nil) (net-name ?nn1&~nil) (min ?min1) (max ?max1) 
            (com ?com) (layer ?lay) (compo ?cpo) (commo ?egarb1))
  (vertical (status nil) (net-name ?nn2&~?nn1&~nil) (min ?min2&:(<= ?min2 ?max1)) 
            (max ?max2&:(>= ?max2 ?min1)) (com ?cpo) (layer ?lay) (compo ?garb1))
  ?h1 <- (horizontal (net-name nil) (min ?min3&:(<= ?min3 ?com)) 
                     (max ?max3&:(>= ?max3 ?cpo)) 
                     (com ?com2&:(and (<= ?com2 ?max1) (>= ?com2 ?min1) 
                                      (<= ?com2 ?max2) (>= ?com2 ?min2)))
                     (layer ?lay) (compo ?cpo2) (commo ?cmo2) (min-net ?mnn) (max-net ?man))
  =>
  (assert (horizontal (min ?min3) (max ?com) (com ?com2) (compo ?cpo2) (commo ?cmo2)
                      (layer ?lay) (min-net ?mnn) (max-net ?nn1)))
  (assert (horizontal (min ?cpo) (max ?max3) (com ?com2) (compo ?cpo2) (commo ?cmo2) 
                      (layer ?lay) (max-net ?man) (min-net ?nn2)))
  (retract ?h1)
)

(defrule p89
  (context (present propagate-constraint))
  (vertical (status nil) (net-name ?nn1&~nil) (min ?garb1) (max ?max1) (com ?com) 
            (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (vertical (status nil) (net-name ?nn2&~?nn1&~nil) (min ?min2) (max ?garb2) 
            (com ?com) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?max1) (< ?qz38 ?min2)))))
  ?v1 <- (vertical (net-name nil) (min ?max1) (max ?min2&:(> ?min2 ?max1)) (com ?com) 
                   (layer ?lay) (compo ?egarb5) (commo ?egarb6))
  =>
  (retract ?v1)
)

(defrule p90
  (context (present propagate-constraint))
  (vertical (status nil) (net-name ?nn1&~nil) (min ?vmin) (max ?garb1) 
            (com ?vcom) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (horizontal (status nil) (net-name ?nn2&~?nn1&~nil) 
              (min ?garb2&:(<= ?garb2 ?vcom)) (max ?garb3&:(>= ?garb3 ?vcom)) 
              (com ?hcom) (layer ?lay) (compo ?vmin) (commo ?garb5))
  ?v1 <-(vertical (net-name nil) (min ?garb4) (max ?vmin&:(> ?vmin ?garb4)) 
                  (com ?vcom) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  =>
  (modify ?v1 (max ?hcom) (max-net ?nn2))
)

(defrule p91
  (context (present propagate-constraint))
  (vertical (status nil) (net-name ?nn1&~nil) (min ?garb1) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (horizontal (status nil) (net-name ?nn2&~?nn1&~nil) (min ?garb2&:(<= ?garb2 ?vcom)) (max ?garb3&:(>= ?garb3 ?vcom)) (com ?hcom) (layer ?lay) (compo ?garb4) (commo ?vmax))
  ?v1 <-(vertical (net-name nil) (min ?vmax) (max ?garb5&:(> ?garb5 ?vmax)) (com ?vcom) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  =>
  (modify ?v1 (min ?hcom) (min-net ?nn2))
)

(defrule p92
  (context (present propagate-constraint))
  (horizontal (status nil) (net-name ?nn1&~nil) (min ?min) (max ?garb1) (com ?hcom) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (vertical (status nil) (net-name ?nn2&~?nn1&~nil) (min ?garb2&:(<= ?garb2 ?hcom)) (max ?garb3&:(>= ?garb3 ?hcom)) (com ?vcom) (layer ?lay) (compo ?min) (commo ?egarb3))
  ?h1 <- (horizontal (net-name nil) (min ?garb4) (max ?min&:(> ?min ?garb4)) (com ?hcom) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  =>
  (modify ?h1 (max ?vcom) (max-net ?nn2))
)

(defrule p93
  (context (present propagate-constraint))
  (horizontal (status nil) (net-name ?nn1&~nil) (min ?garb1) (max ?max) (com ?hcom) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (vertical (status nil) (net-name ?nn2&~?nn1&~nil) (min ?garb2&:(<= ?garb2 ?hcom)) (max ?garb3&:(>= ?garb3 ?hcom)) (com ?vcom) (layer ?lay) (compo ?garb4) (commo ?max))
  ?h1 <- (horizontal (net-name nil) (min ?max) (max ?garb4&:(> ?garb4 ?max)) (com ?hcom) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  =>
  (modify ?h1 (min ?vcom) (min-net ?nn2))
)

(defrule p94
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  ?h1 <- (horizontal (net-name nil) (min ?garb3) (max ?gx&:(> ?gx ?garb3)) (com ?gy) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  (vertical (status nil) (net-name ?nn2&~?nn&~nil) (min ?garb1&:(<= ?garb1 ?gy)) (max ?garb2&:(>= ?garb2 ?gy)) (com ?vcom) (layer ?lay) (compo ?gx) (commo ?egarb2))
  =>
  (modify ?h1 (max ?vcom) (max-net ?nn2))
)

(defrule p95
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  ?h1 <- (horizontal (net-name nil) (min ?gx) (max ?garb4&:(> ?garb4 ?gx)) (com ?gy) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (vertical (status nil) (net-name ?nn2&~?nn&~nil) (min ?garb1&:(<= ?garb1 ?gy)) (max ?garb2&:(>= ?garb2 ?gy)) (com ?vcom) (layer ?lay) (compo ?garb3) (commo ?gx))
  =>
  (modify ?h1 (min ?vcom) (min-net ?nn2))
)

(defrule p96
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  (horizontal (status nil) (net-name ?nn2&~?nn&~nil) (min ?garb1&:(<= ?garb1 ?gx)) (max ?garb2&:(>= ?garb2 ?gx)) (com ?hcom) (layer ?lay) (compo ?gy) (commo ?garb3))
  ?v1 <-(vertical (net-name nil) (min ?garb4) (max ?gy&:(> ?gy ?garb4)) (com ?gx) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  =>
  (modify ?v1 (max ?hcom) (max-net ?nn2))
)

(defrule p97
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  (horizontal (status nil) (net-name ?nn2&~?nn&~nil) (min ?garb1&:(<= ?garb1 ?gx)) (max ?garb2&:(>= ?garb2 ?gx)) (com ?hcom) (layer ?lay) (compo ?garb3) (commo ?gy))
  ?v1 <-(vertical (net-name nil) (min ?gy) (max ?garb4&:(> ?garb4 ?gy)) (com ?gx) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  =>
  (modify ?v1 (min ?hcom) (min-net ?nn2))
)

(defrule p98
  (context (present propagate-constraint | extend-pins | random1 | check-for-routed-net))
  ?h1 <- (horizontal (min ?min) (max ?qx1&:(<= ?qx1 ?min)))
  =>
  (retract ?h1)
)

(defrule p99
  (context (present propagate-constraint | extend-pins | random1 | check-for-routed-net))
  ?v1 <- (vertical (min ?min) (max ?qx1&:(<= ?qx1 ?min)))
  =>
  (retract ?v1)
)

(defrule p100
  ?h1 <- (horizontal (min ?min) (max ?qx1&:(<= ?qx1 ?min)))
  =>
  (retract ?h1)
)

(defrule p101
  ?v1 <- (vertical (min ?min) (max ?qx1&:(<= ?qx1 ?min)))
  =>
  (retract ?v1)
)

(defrule p102
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?min1) (max ?max1&:(> ?max1 ?min1)) (com ?com1) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (vertical (status nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?min1)))
  (not (horizontal (status nil) (min ?qz20&:(< ?qz20 ?min1)) (max ?qw63&:(>= ?qw63 ?min1)) (com ?com1)))
  (vertical (status nil) (net-name ?garb1) (min ?vmin&:(<= ?vmin ?com1)) 
            (max ?qz60&:(and (>= ?qz60 ?com1) (> ?qz60 ?vmin))) 
            (com ?com2&:(and (> ?com2 ?min1) (<= ?com2 ?max1))) 
            (layer ?garb2) (compo ?egarb4) (commo ?egarb5))
  (not (vertical (status nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1)))))
  =>
  (modify ?h1 (min ?com2) (min-net nil))
)

(defrule p103
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?min1) (max ?max1&:(> ?max1 ?min1)) (com ?com1) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (horizontal (status nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?min1)))
  (not (vertical (status nil) (min ?qz20&:(< ?qz20 ?min1)) (max ?qw63&:(>= ?qw63 ?min1)) (com ?com1)))
  (horizontal (status nil) (net-name ?garb1) (min ?hmin&:(<= ?hmin ?com1)) (max ?garb2&:(and (>= ?garb2 ?com1) (> ?garb2 ?hmin))) (com ?com2&:(and (> ?com2 ?min1) (<= ?com2 ?max1))) (layer ?garb3) (compo ?egarb4) (commo ?egarb5))
  (not (horizontal (status nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw64&:(>= ?qw64 ?com1)) (com ?qz47&:(and (< ?qz47 ?com2) (>= ?qz47 ?min1)))))
  =>
  (modify ?v1 (min ?com2) (min-net nil))
)

(defrule p104
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?min1) (max ?max1&:(> ?max1 ?min1)) (com ?com1) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  (not (vertical (status nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?max1)))
  (not (horizontal (status nil) (min ?qw11&:(<= ?qw11 ?max1)) (max ?qw86&:(> ?qw86 ?max1)) (com ?com1)))
  (vertical (status nil) (net-name ?garb1) (min ?vmin&:(<= ?vmin ?com1)) (max ?garb2&:(and (>= ?garb2 ?com1) (> ?garb2 ?vmin))) 
            (com ?com2&:(and (>= ?com2 ?min1) (< ?com2 ?max1))) 
            (layer ?egarb1) (compo ?egarb2) (commo ?egarb3))
  (not (vertical (status nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw63&:(>= ?qw63 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1)))))
  =>
  (modify ?h1 (max ?com2) (max-net nil))
)

(defrule p105
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?min1) (max ?max1&:(> ?max1 ?min1)) (com ?com1) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (horizontal (status nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw63&:(>= ?qw63 ?com1)) (com ?max1)))
  (not (vertical (status nil) (min ?qw11&:(<= ?qw11 ?max1)) (max ?qw86&:(> ?qw86 ?max1)) (com ?com1)))
  (horizontal (status nil) (net-name ?garb1) (min ?hmin&:(<= ?hmin ?com1)) (max ?garb2&:(and (>= ?garb2 ?com1) (> ?garb2 ?hmin))) (com ?com2&:(and (>= ?com2 ?min1) (< ?com2 ?max1))) (layer ?egarb1) (compo ?egarb4) (commo ?egarb5))
  (not (horizontal (status nil) (min ?qw75&:(<= ?qw75 ?com1)) (max ?qw62&:(>= ?qw62 ?com1)) (com ?qz30&:(and (> ?qz30 ?com2) (<= ?qz30 ?max1)))))
  =>
  (modify ?v1 (max ?com2) (max-net nil))
)

(defrule p106
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?hmin) (max ?hmax&:(> ?hmax ?hmin)) (com ?hcom) (layer ?lay) (compo ?egarb6) (commo ?egarb7))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?hmin) (< ?qz38 ?hmax)))))
  (not (vertical (status nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?hmax) (layer ?lay)))
  (not (horizontal (status nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom) (layer ?lay)))
  (vertical (net-name ?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?hmax) (layer ~?lay) (compo ?egarb4) (commo ?egarb5))
  (vertical (net-name ~?nn1&~nil) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?hmin) (layer ~?lay) (compo ?egarb2) (commo ?egarb3))
  =>
  (retract ?h1)
)


(defrule p107
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?vmin) (max ?vmax&:(> ?vmax ?vmin)) (com ?vcom) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?vmin) (< ?qz38 ?vmax)))))
  (not (horizontal (status nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?vmax) (layer ?lay)))
  (not (vertical (status nil) (min ?vmax) (max ?qw86&:(> ?qw86 ?vmax)) (com ?vcom) (layer ?lay)))
  (horizontal (net-name ?nn1&~nil) (min ?garb1&:(<= ?garb1 ?vcom)) (max ?garb2&:(>= ?garb2 ?vcom)) (com ?vmax) (layer ?garb3&~?lay) (compo ?egarb6) (commo ?egarb7))
  (horizontal (net-name ~?nn1&~nil) (min ?garb4&:(<= ?garb4 ?vcom)) (max ?garb5&:(>= ?garb5 ?vcom)) (com ?vmin) (layer ?garb6&~?lay) (compo ?egarb4) (commo ?egarb5))
  =>
  (retract ?v1)
)

(defrule p108
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?hmin) (max ?hmax&:(> ?hmax ?hmin)) (com ?hcom) (layer ?lay) (compo ?egarb6) (commo ?egarb7))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?hmin) (< ?qz38 ?hmax)))))
  (not (vertical (status nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?hmin) (layer ?lay)))
  (not (horizontal (status nil) (min ?qz20&:(< ?qz20 ?hmin)) (max ?hmin) (com ?hcom) (layer ?lay)))
  (not (vertical (status nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?hmax) (layer ?lay)))
  (not (horizontal (status nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom) (layer ?lay)))
  (vertical (net-name ?nn1&~nil) (min ?garb1&:(<= ?garb1 ?hcom)) (max ?garb2&:(>= ?garb2 ?hcom)) (com ?hmax) (layer ?garb3&~?lay) (compo ?egarb2) (commo ?egarb3))
  (vertical (net-name ?garb7&~?nn1&~nil) (min ?gabr4&:(<= ?gabr4 ?hcom)) (max ?garb5&:(>= ?garb5 ?hcom)) (com ?hmin) (layer ?garb6&~?lay) (compo ?egarb4) (commo ?egarb5))
  =>
  (retract ?h1)
)

(defrule p109
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?vmin) (max ?vmax&:(> ?vmax ?vmin)) (com ?vcom) (layer ?lay) (compo ?egarb6) (commo ?egarb7))
  (not (horizontal (com ?qz38&:(and (> ?qz38 ?vmin) (< ?qz38 ?vmax)))))
  (not (horizontal (status nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?vmin) (layer ?lay)))
  (not (vertical (status nil) (min ?qz20&:(< ?qz20 ?vmin)) (max ?vmin) (com ?vcom) (layer ?lay)))
  (horizontal (net-name ?nn1&~nil) (min ?garb1&:(<= ?garb1 ?vcom)) (max ?garb2&:(>= ?garb2 ?vcom)) (com ?vmax) (layer ?garb3&~?lay) (compo ?egarb2) (commo ?egarb3))
  (horizontal (net-name ?garb7&~?nn1&~nil) (min ?garb4&:(<= ?garb4 ?vcom)) (max ?garb5&:(>= ?garb5 ?vcom)) (com ?vmin) (layer ?garb6&~?lay) (compo ?egarb4) (commo ?egarb5))
  =>
  (retract ?v1)
)

(defrule p110
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?max2&:(> ?max2 ?max1)) (com ?hcom) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  (not (vertical (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?max2) (layer ?lay)))
  (not (horizontal (min ?max2) (max ?qw86&:(> ?qw86 ?max2)) (com ?hcom) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw87&:(> ?qw87 ?hcom)) (com ?qz38&:(and (> ?qz38 ?max1) (< ?qz38 ?max2)))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?qz39&:(and (> ?qz39 ?max1) (< ?qz39 ?max2))) (max-net nil)))
  (not (vertical (net-name nil) (min ?hcom) (max ?qw88&:(> ?qw88 ?hcom)) (com ?qz40&:(and (> ?qz40 ?max1) (< ?qz40 ?max2))) (min-net nil)))
  (horizontal (net-name ?nn&~nil) (min ?garb1) (max ?max1) (com ?hcom) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (vertical (net-name ?nn1&~?nn&~nil) (min ?garb4&:(<= ?garb4 ?hcom)) (max ?garb2&:(>= ?garb2 ?hcom)) (com ?max2) (layer ?garb3&~?lay) (compo ?egarb6) (commo ?egarb7))
  (not (vertical (net-name ?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?qz41&:(and (> ?qz41 ?max1) (< ?qz41 ?max2)))))
  =>
  (modify ?h1 (max =(- ?max2 1)) (max-net nil))
)

(defrule p111
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?max2&:(> ?max2 ?max1)) (com ?hcom) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  (not (vertical (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?max1) (layer ?lay)))
  (not (horizontal (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?hcom) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw86&:(> ?qw86 ?hcom)) (com ?qz39&:(and (> ?qz39 ?max1) (< ?qz39 ?max2)))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?qz40&:(and (> ?qz40 ?max1) (< ?qz40 ?max2))) (max-net nil)))
  (not (vertical (net-name nil) (min ?hcom) (max ?qw87&:(> ?qw87 ?hcom)) (com ?qz41&:(and (> ?qz41 ?max1) (< ?qz41 ?max2))) (min-net nil)))
  (horizontal (net-name ?nn&~nil) (min ?max2) (max ?garb1) (com ?hcom) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (vertical (net-name ?nn1&~?nn&~nil) (min ?garb4&:(<= ?garb4 ?hcom)) (max ?garb2&:(>= ?garb2 ?hcom)) (com ?max1) (layer ?garb3&~?lay) (compo ?egarb6) (commo ?egarb7))
  (not (vertical (net-name ?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?qz42&:(and (> ?qz42 ?max1) (< ?qz42 ?max2)))))
  =>
  (modify ?h1 (min =(+ ?max1 1)) (min-net nil))
)

(defrule p112
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?max2&:(> ?max2 ?max1)) (com ?hcom) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  (not (vertical (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?max2) (layer ?lay)))
  (not (horizontal (min ?max2) (max ?qw86&:(> ?qw86 ?max2)) (com ?hcom) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw87&:(> ?qw87 ?hcom)) (com ?qz49&:(and (>= ?qz49 ?max1) (< ?qz49 ?max2)))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?qz50&:(and (>= ?qz50 ?max1) (< ?qz50 ?max2))) (max-net nil)))
  (not (vertical (net-name nil) (min ?hcom) (max ?qw88&:(> ?qw88 ?hcom)) (com ?qz51&:(and (>= ?qz51 ?max1) (< ?qz51 ?max2))) (min-net nil)))
  (not (horizontal (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?hcom) (layer ?lay)))
  (vertical (net-name ?nn1&~nil) (min ?garb1&:(<= ?garb1 ?hcom)) (max ?garb2&:(>= ?garb2 ?hcom)) (com ?max2) (layer ?garb3&~?lay) (compo ?egarb6) (commo ?egarb7))
  (not (vertical (net-name ?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?qz52&:(and (>= ?qz52 ?max1) (< ?qz52 ?max2)))))
  =>
  (modify ?h1 (max =(- ?max2 1)) (max-net nil))
)

(defrule p113
  (context (present propagate-constraint))
  ?h1 <- (horizontal (net-name nil) (min ?max1) (max ?max2&:(> ?max2 ?max1)) (com ?hcom) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  (not (vertical (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?max1) (layer ?lay)))
  (not (horizontal (min ?qw9&:(< ?qw9 ?max1)) (max ?max1) (com ?hcom) (layer ?lay)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw86&:(> ?qw86 ?hcom)) (com ?qz30&:(and (> ?qz30 ?max1) (<= ?qz30 ?max2)))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?qz31&:(and (> ?qz31 ?max1) (<= ?qz31 ?max2))) (max-net nil)))
  (not (vertical (net-name nil) (min ?hcom) (max ?qw87&:(> ?qw87 ?hcom)) (com ?qz32&:(and (> ?qz32 ?max1) (<= ?qz32 ?max2))) (min-net nil)))
  (not (horizontal (min ?max2) (max ?qw88&:(> ?qw88 ?max2)) (com ?hcom) (layer ?lay)))
  (vertical (net-name ?nn1&~nil) (min ?garb1&:(<= ?garb1 ?hcom)) (max ?garb2&:(>= ?garb2 ?hcom)) (com ?max1) (layer ?garb3&~?lay) (compo ?egarb6) (commo ?egarb7))
  (not (vertical (net-name ?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?qz33&:(and (> ?qz33 ?max1) (<= ?qz33 ?max2)))))
  =>
  (modify ?h1 (min =(+ ?max1 1)) (min-net nil))
)



(defrule p114
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?max1) (max ?max2&:(> ?max2 ?max1)) (com ?hcom) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  (vertical (net-name ?nn&~nil) (min ?garb1) (max ?max1) (com ?hcom) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (horizontal (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?max2) (layer ?lay)))
  (not (vertical (min ?max2) (max ?qw86&:(> ?qw86 ?max2)) (com ?hcom) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw87&:(> ?qw87 ?hcom)) (com ?qz38&:(and (> ?qz38 ?max1) (< ?qz38 ?max2)))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?hcom)) (max ?hcom) (com ?qz39&:(and (> ?qz39 ?max1) (< ?qz39 ?max2))) (max-net nil)))
  (not (horizontal (net-name nil) (min ?hcom) (max ?qw88&:(> ?qw88 ?hcom)) (com ?qz40&:(and (> ?qz40 ?max1) (< ?qz40 ?max2))) (min-net nil)))
  (horizontal (net-name ?nn1&~?nn&~nil) (min ?garb2&:(<= ?garb2 ?hcom)) (max ?garb3&:(>= ?garb3 ?hcom)) (com ?max2) (layer ?garb4&~?lay) (compo ?egarb6) (commo ?egarb7))
  (not (horizontal (net-name ?nn1&~nil) (min ?qw75&:(<= ?qw75 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?qz41&:(and (> ?qz41 ?max1) (< ?qz41 ?max2)))))
  =>
  (modify ?v1 (max =(- ?max2 1)) (max-net nil))
)

(defrule p115
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (compo ?gy)))
  ?v1 <-(vertical (net-name nil) (min ?vmin) (max ?gy&:(> ?gy ?vmin)) (com ?gx) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz49&:(and (>= ?qz49 ?vmin) (< ?qz49 ?gy)))))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx)) (max ?gx) (com ?qz50&:(and (>= ?qz50 ?vmin) (< ?qz50 ?gy))) (max-net nil)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw88&:(> ?qw88 ?gx)) (com ?qz51&:(and (>= ?qz51 ?vmin) (< ?qz51 ?gy))) (min-net nil)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz52&:(and (>= ?qz52 ?vmin) (< ?qz52 ?gy)))))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?vmin)) (max ?qw62&:(>= ?qw62 ?vmin)) (com ?gx)))
  =>
  (modify ?v1 (max =(- ?gy 1)) (max-net nil))
)

(defrule p116
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?egarb1))
  ?h1 <- (horizontal (net-name nil) (min ?gx) (max ?max2&:(> ?max2 ?gx)) (com ?gy) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (not (vertical (net-name nil) (min ?gy) (max ?qw86&:(> ?qw86 ?gy)) (com ?qz30&:(and (> ?qz30 ?gx) (<= ?qz30 ?max2))) (min-net nil)))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?gy)) (max ?qw87&:(> ?qw87 ?gy)) (com ?qz31&:(and (> ?qz31 ?gx) (<= ?qz31 ?max2)))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?gy)) (max ?gy) (com ?qz32&:(and (> ?qz32 ?gx) (<= ?qz32 ?max2))) (max-net nil)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?qz33&:(and (> ?qz33 ?gx) (<= ?qz33 ?max2)))))
  =>
  (modify ?h1 (min =(+ ?gx 1)) (min-net nil))
)

(defrule p117
  (context (present propagate-constraint))
  ?v1 <-(vertical (net-name nil) (min ?vmin) (max ?vmax&:(> ?vmax ?vmin)) (com ?vcom) 
                  (layer ?lay) (min-net ?nn&~nil))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) 
              (max ?hmax&:(and (>= ?hmax ?vcom) (> ?hmax ?hmin))) (com ?vmax) 
              (layer ?lay) (commo ?vmin) (min-net ~?nn&~nil) (max-net ~?nn&~nil))
  (not (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?vmax)) (max ?qw86&:(> ?qw86 ?vmax)) (com ?qz38&:(and (> ?qz38 ?hmin) (< ?qz38 ?hmax)))))
  (not (vertical (net-name nil) (min ?qz21&:(< ?qz21 ?vmax)) (max ?vmax) (com ?qz39&:(and (> ?qz39 ?hmin) (< ?qz39 ?vcom))) (max-net nil)))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?vmax)) (max ?vmax) (com ?qz40&:(and (> ?qz40 ?vcom) (< ?qz40 ?hmax))) (max-net nil)))
  (not (vertical (net-name nil) (min ?vmax) (max ?qw87&:(> ?qw87 ?vmax)) (com ?qz41&:(and (> ?qz41 ?hmin) (< ?qz41 ?hmax))) (min-net nil)))
  (not (vertical (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?vmax)) (max ?qw62&:(>= ?qw62 ?vmax)) (com ?qz42&:(and (> ?qz42 ?hmin) (< ?qz42 ?hmax)))))
  =>
  (retract ?v1)
)

(defrule p118
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay1) (compo ?cpo1) (commo ?cmo1))
  (not (vertical (net-name nil) (com ?com1)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw86&:(> ?qw86 ?com1)) (com ?qw87&:(> ?qw87 ?max1))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qw1&:(< ?qw1 ?min1))))
  ?h1 <- (horizontal (net-name nil) (min ?min2&:(< ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?com1) (> ?max2 ?min2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1))) (layer ?lay2&~?lay1) (compo ?cpo2) (commo ?cmo2))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?com1)) (max ?qw89&:(> ?qw89 ?com1)) (com ?qw60&:(and (>= ?qw60 ?min1) (<= ?qw60 ?max1))) (layer ?lay2)))
  ?t1 <-  (to-be-routed (net-name ?nn2&~?nn1) (no-of-attached-pins ?nap))
  (not (horizontal (status nil) (net-name ?nn2&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (pin (net-name ?nn2) (pin-x ?qz23&:(< ?qz23 ?com1)))
  (pin (net-name ?nn2) (pin-x ?qw90&:(> ?qw90 ?com1)))
  ?b1 <- (branch-no ?pn)
  =>
  (assert (branch-no =(+ ?pn 1)))
  (modify ?t1 (no-of-attached-pins =(- ?nap 1)))
  (retract ?b1)
  (assert (horizontal (min ?cmo1) (max ?cpo1) (com ?com2) (layer ?lay2) (net-name ?nn2) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (horizontal (min ?min2) (max ?cmo1) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (max-net ?nn2)))
  (assert (horizontal (min ?cpo1) (max ?max2) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (min-net ?nn2)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?cmo1) (grid-y ?com2) (grid-layer ?lay2) (came-from east)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?cpo1) (grid-y ?com2) (grid-layer ?lay2) (came-from west)))
  (retract ?h1)
)

(defrule p119
  (context (present propagate-constraint))
  (vertical (net-name ?nn1&~nil) (min ?min1) (max ?max1) (com ?com1) (layer ?lay1) (compo ?cpo1) (commo ?cmo1))
  (not (vertical (net-name nil) (com ?com1)))
  ?h1 <- (horizontal (net-name nil) (min ?min2&:(< ?min2 ?com1)) (max ?max2&:(and (> ?max2 ?com1) (> ?max2 ?min2))) (com ?com2&:(and (>= ?com2 ?min1) (<= ?com2 ?max1))) (layer ?lay2&~?lay1) (compo ?cpo2) (commo ?cmo2))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?com1)) (max ?qw86&:(> ?qw86 ?com1)) (com ?qw87&:(> ?qw87 ?com2))))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?com1)) (max ?qw88&:(> ?qw88 ?com1)) (com ?qz22&:(< ?qz22 ?com2))))
  ?t1 <-  (to-be-routed (net-name ?nn2&~?nn1) (no-of-attached-pins ?nap))
  (not (horizontal (status nil) (net-name ?nn2&~nil) (min ?qw74&:(<= ?qw74 ?com1)) (max ?qw62&:(>= ?qw62 ?com1))))
  (pin (net-name ?nn2) (pin-x ?qz23&:(< ?qz23 ?com1)))
  (pin (net-name ?nn2) (pin-x ?qw89&:(> ?qw89 ?com1)))
  ?b1 <- (branch-no ?pn)
  =>
  (assert (branch-no =(+ ?pn 1)))
  (modify ?t1 (no-of-attached-pins =(- ?nap 1)))
  (retract ?b1)
  (assert (horizontal (min ?cmo1) (max ?cpo1) (com ?com2) (layer ?lay2) (net-name ?nn2) (pin-name ?pn) (commo ?cmo2) (compo ?cpo2)))
  (assert (horizontal (min ?min2) (max ?cmo1) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (max-net ?nn2)))
  (assert (horizontal (min ?cpo1) (max ?max2) (com ?com2) (layer ?lay2) (commo ?cmo2) (compo ?cpo2) (min-net ?nn2)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?cmo1) (grid-y ?com2) (grid-layer ?lay2) (came-from east)))
  (assert (ff (net-name ?nn2) (pin-name ?pn) (grid-x ?cpo1) (grid-y ?com2) (grid-layer ?lay2) (came-from west)))
  (retract ?h1)
)
