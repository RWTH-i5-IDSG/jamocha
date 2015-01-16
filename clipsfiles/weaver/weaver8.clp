
(defrule p160
  (context (present propagate-constraint))
  ?ff1 <- (ff (can-chng-layer ~no) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  (not (vertical (net-name ~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ~?lay)))
  (not (horizontal (net-name ~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy) (layer ~?lay)))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?qz60&:(and (>= ?qz60 ?gx) (> ?qz60 ?hmin))) (com ?gy) (layer ?lay2&~?lay))
  =>
  (modify ?ff1 (grid-layer ?lay2) (can-chng-layer no))
)

(defrule p161
  (context (present propagate-constraint))
  ?ff1 <- (ff (can-chng-layer ~no) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay))
  (not (horizontal (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ~?lay)))
  (not (vertical (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx) (layer ~?lay)))
  =>
  (modify ?ff1 (can-chng-layer no))
)

(defrule p162
  (context (present propagate-constraint | move-ff))
  ?ff1 <- (ff (can-chng-layer ~no) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay))
  (horizontal (net-name ~?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ~?lay))
  =>
  (modify ?ff1 (can-chng-layer no))
)

(defrule p163
  (context (present propagate-constraint | move-ff))
  ?ff1 <- (ff (can-chng-layer ~no) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay))
  (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ~?lay))
  =>
  (modify ?ff1 (can-chng-layer no))
)

(defrule p164
  (context (present propagate-constraint))
  ?ff1 <- (ff (can-chng-layer no) (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay1) (pin-name ?pn))
  ?ff2 <- (ff (can-chng-layer no) (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?lay2) (pin-name ?garb1))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy1)) (max ?qw62&:(>= ?qw62 ?gy1)) (com ?gx2)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com ?gy1)))
  ?h1 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx2)) (max ?hmax&:(and (>= ?hmax ?gx1) (> ?hmax ?hmin))) (com ?gy1) (layer ?lay1) (compo ?hcpo) (commo ?gy2) (min-net ?hnn1))
  ?v1 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy2)) (max ?vmax&:(and (>= ?vmax ?gy1) (> ?vmax ?vmin))) (com ?gx2) (layer ?lay2) (compo ?gx1) (commo ?vcmo) (min-net ?vnn1))
  =>
  (assert (horizontal (min ?hmin) (max ?gx2) (com ?gy1) (commo ?gy2) (compo ?hcpo) (layer ?lay1) (min-net ?hnn1) (max-net ?nn)))
  (modify ?h1 (min ?gx1) (min-net ?nn))
  (assert (vertical (min ?vmin) (max ?gy2) (com ?gx2) (commo ?vcmo) (compo ?gx1) (layer ?lay2) (min-net ?vnn1) (max-net ?nn)))
  (modify ?v1 (min ?gy1) (min-net ?nn))
  (retract ?ff1 ?ff2)
  (assert (horizontal (min ?gx2) (max ?gx1) (com ?gy1) (commo ?gy2) (compo ?hcpo) (layer ?lay1) (net-name ?nn) (pin-name ?pn)))
  (assert (vertical (min ?gy2) (max ?gy1) (com ?gx2) (commo ?vcmo) (compo ?gx1) (layer ?lay2) (net-name ?nn) (pin-name ?pn)))
)

(defrule p165
  (context (present propagate-constraint | extend-pins | set-min-max))
  (vertical (status nil) (net-name ?nn&~nil) (min ?max) (max ?garb1) (com ?com) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?v1 <- (vertical (net-name nil) (min ?garb2) (max ?max&:(> ?max ?garb2)) (com ?com) (layer ?lay) (max-net ~?nn))
  =>
  (modify ?v1 (max-net ?nn))
)

(defrule p166
  (context (present propagate-constraint | extend-pins | set-min-max))
  (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?min) (com ?com) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?v1 <- (vertical (net-name nil) (min ?min) (max ?garb2&:(> ?garb2 ?min)) (com ?com) (layer ?lay) (min-net ~?nn))
  =>
  (modify ?v1 (min-net ?nn))
)

(defrule p167
  (context (present propagate-constraint | extend-pins | set-min-max))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?max) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?v1 <- (vertical (net-name nil) (min ?garb1&:(< ?garb1 ?max)) (max ?max) (com ?qw60&:(and (>= ?qw60 ?hmin) (<= ?qw60 ?hmax))) (layer ?lay) (max-net ~?nn))
  =>
  (modify ?v1 (max-net ?nn))
)

(defrule p168
  (context (present propagate-constraint | extend-pins | set-min-max))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?min) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?v1 <- (vertical (net-name nil) (min ?min) (max ?garb1&:(> ?garb1 ?min)) (com ?qw60&:(and (>= ?qw60 ?hmin) (<= ?qw60 ?hmax))) (layer ?lay) (min-net ~?nn))
  =>
  (modify ?v1 (min-net ?nn))
)

(defrule p169
  (context (present propagate-constraint | extend-pins | set-min-max))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?max) (max ?garb1) (com ?com) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?h1 <- (horizontal (net-name nil) (min ?garb2) (max ?max&:(> ?max ?garb2)) (com ?com) (layer ?lay) (max-net ~?nn))
  =>
  (modify ?h1 (max-net ?nn))
)

(defrule p170
  (context (present propagate-constraint | extend-pins | set-min-max))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?min) (com ?com) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?h1 <- (horizontal (net-name nil) (min ?min) (max ?garb2&:(> ?garb2 ?min)) (com ?com) (layer ?lay) (min-net ~?nn))
  =>
  (modify ?h1 (min-net ?nn))
)

(defrule p171
  (context (present propagate-constraint | extend-pins | set-min-max))
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?max) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?h1 <- (horizontal (net-name nil) (min ?garb1) (max ?max&:(> ?max ?garb1)) (com ?qw60&:(and (>= ?qw60 ?vmin) (<= ?qw60 ?vmax))) (layer ?lay) (max-net ~?nn))
  =>
  (modify ?h1 (max-net ?nn))
)

(defrule p172
  (context (present propagate-constraint | extend-pins | set-min-max))
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?min) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  ?h1 <- (horizontal (net-name nil) (min ?min) (max ?garb1&:(> ?garb1 ?min)) (com ?qw60&:(and (>= ?qw60 ?vmin) (<= ?qw60 ?vmax))) (layer ?lay) (min-net ~?nn))
  =>
  (modify ?h1 (min-net ?nn))
)

(defrule p173
  (context (present propagate-constraint))
  ?ff1 <- (ff (can-chng-layer no) (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) (pin-name ?pn))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1&:(<= ?garb1 ?gx)) (max ?garb2&:(>= ?garb2 ?gx)) (com ?gy2) (layer ?garb3) (compo ?garb4) (commo ?gy1) (pin-name ~?pn))
  (not (horizontal (net-name ~nil&~?nn) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy2)))
  (not (vertical (net-name ~nil&~?nn) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx)))
  ?v1 <- (vertical (net-name nil) (min ?min&:(<= ?min ?gy1)) (max ?max&:(and (>= ?max ?gy2) (> ?max ?min))) (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (min-net ?nn1))
  =>
  (retract ?ff1)
  (assert (vertical (min ?min) (max ?gy1) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (min-net ?nn1) (max-net ?nn)))
  (modify ?v1 (min ?gy2) (min-net ?nn))
  (assert (vertical (min ?gy1) (max ?gy2) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?lay) (net-name ?nn) (pin-name ?pn)))
)

(defrule p174
  (join-routed-net ?nn)
  ?h1 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) (com ?com) (layer ?lay) (compo ?garb1) (commo ?garb2))
  ?h2 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?max) (max ?max2) (com ?com) (layer ?lay) (compo ?garb3) (commo ?garb4))
  =>
  (modify ?h1 (max ?max2))
  (retract ?h2)
)

(defrule p175
  (join-routed-net ?nn)
  ?v1 <- (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?max) (com ?com) (layer ?lay) (compo ?garb2) (commo ?garb3))
  ?v2 <- (vertical (status nil) (net-name ?nn&~nil) (min ?max) (max ?max2) (com ?com) (layer ?lay) (compo ?garb4) (commo ?garb5))
  =>
  (modify ?v1 (max ?max2))
  (retract ?v2)
)

(defrule p176
  ?j1 <- (join-routed-net ?nn)
  =>
  (retract ?j1)
  (assert (move-via ?nn))
)


(defrule p177
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?vlayer) (pin-name ?egarb1))
  (horizontal (net-name nil) (min ?hmin2) (max ?gx&:(> ?gx ?hmin2)) (com ?vmax) (layer ?vlayer) (compo ?egarb8) (commo ?egarb9))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw86&:(> ?qw86 ?gx)) (com ?qz61&:(and (< ?qz61 ?vmax) (> ?qz61 ?gy)))))
  ?v1 <- (vertical (net-name nil) (min ?garb1&:(<= ?garb1 ?gy)) (max ?vmax&:(and (> ?vmax ?garb1) (> ?vmax ?gy))) (com ?gx) (layer ?vlayer) (commo ?cmo) (compo ?cpo) (max-net ?maxn))
  (horizontal (net-name ~?nn&~nil) (min ?hmin1&:(<= ?hmin1 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?vmax) (layer ~?vlayer))
  (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?vmax)) (max ?qw62&:(>= ?qw62 ?vmax)) 
            (com ?vcom&:(and (< ?vcom ?gx) (>= ?vcom ?hmin1) (>= ?vcom ?hmin2))) 
            (layer ?vlayer))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw87&:(> ?qw87 ?gx)) (com ?vmax)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?gx) (com ?qz62&:(and (< ?qz62 ?vmax) (> ?qz62 ?gy))) (max-net nil)))
  (not (vertical (net-name nil) (min ?vmax) (max ?qw88&:(> ?qw88 ?vmax)) (com ?gx)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?vmax) (max ?qw89&:(> ?qw89 ?vmax)) (com ?gx)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?gx) (max ?qw90&:(> ?qw90 ?gx)) (com ?vmax)))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw91&:(> ?qw91 ?gx)) (com ?qz63&:(and (< ?qz63 ?vmax) (> ?qz63 ?gy))) (min-net nil)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qz64&:(and (< ?qz64 ?vmax) (> ?qz64 ?gy)))))
  (not (vertical (net-name nil) (min ?qz22&:(< ?qz22 ?vmax)) (max ?qw92&:(> ?qw92 ?vmax)) (com ?qz65&:(and (< ?qz65 ?gx) (> ?qz65 ?vcom)))))
  (not (vertical (net-name nil) (min ?qz23&:(< ?qz23 ?vmax)) (max ?vmax) (com ?qz66&:(and (< ?qz66 ?gx) (> ?qz66 ?vcom))) (max-net nil)))
  (not (vertical (net-name nil) (min ?vmax) (max ?qw93&:(> ?qw93 ?vmax)) (com ?qz67&:(and (< ?qz67 ?gx) (> ?qz67 ?vcom))) (min-net nil)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw77&:(<= ?qw77 ?vmax)) (max ?qw64&:(>= ?qw64 ?vmax)) (com ?qz68&:(and (< ?qz68 ?gx) (> ?qz68 ?vcom)))))
  =>
  (assert (vertical (min =(+ ?gy 1)) (max ?vmax) (com ?gx) (commo ?cmo) (compo ?cpo) (layer ?vlayer) (max-net ?maxn)))
  (modify ?v1 (max ?gy) (max-net ?nn))
)

(defrule p178
  (context (present propagate-constraint))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?layer) (pin-name ?pn))
  ?h1 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?hmax&:(and (> ?hmax ?gx) (> ?hmax ?hmin))) (com ?gy) (layer ?layer) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (vertical (net-name ~?nn&~nil) (min ?cpo) (max ?garb2&:(>= ?garb2 ?cpo)) (com ?vcom) (layer ?layer1) (compo ?vcpo) (commo ?gx))
  (horizontal (net-name ~?nn&~nil) (min ?garb3&:(<= ?garb3 ?vcom)) (max ?garb4&:(>= ?garb4 ?vcom)) (com ?cpo) (layer ~?layer1) (compo ?garb5) (commo ?gy))
  (vertical (net-name ~?nn&~nil) (min ?garb6&:(<= ?garb6 ?cmo)) (max ?cmo) (com ?vcom) (layer ?layer2) (compo ?vcpo) (commo ?gx))
  (horizontal (net-name ~?nn&~nil) (min ?garb7&:(<= ?garb7 ?vcom)) (max ?garb8&:(>= ?garb8 ?vcom)) (com ?cmo) (layer ~?layer2) (compo ?gy) (commo ?garb9))
  (vertical (net-name ~?nn&~nil) (min ?garb13&:(<= ?garb13 ?gy)) (max ?garb14&:(>= ?garb14 ?gy)) (com ?vcpo) (layer ?layer3) (compo ?garb10) (commo ?vcom))
  (horizontal (net-name ~?nn&~nil) (min ?garb11&:(<= ?garb11 ?vcpo)) (max ?garb12&:(>= ?garb12 ?vcpo)) (com ?gy) (layer ~?layer3) (compo ?cpo) (commo ?cmo))
  =>
  (assert (horizontal (min =(+ ?gx 1)) (max ?hmax) (com ?gy) (compo ?cpo) (commo ?cmo) (layer ?layer) (max-net ?mn)))
  (modify ?h1 (max ?gx) (max-net ?nn))
)

(defrule p179
  (context (present propagate-constraint))
  ?ff1 <- (ff (came-from west) (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (pin-name ?pn))
  (vertical (net-name ~?nn&~nil) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(>= ?vmax ?gy)) (com ?vcom) (layer ?lay2) (compo ?garb1) (commo ?gx))
  (vertical (net-name ~?nn&~nil) (min ?vmax2&:(and (> ?vmax2 ?gy) (<= ?vmax2 ?vmax))) (max ?garb4) (com ?gx) (layer ?lay4))
  (vertical (net-name ~?nn&~nil) (min ?garb2) (max ?vmin2&:(and (< ?vmin2 ?gy) (>= ?vmin2 ?vmin))) (com ?gx) (layer ?lay3))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw87&:(> ?qw87 ?gx)) (com ?qz38&:(and (> ?qz38 ?vmin2) (< ?qz38 ?gy)))))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw88&:(> ?qw88 ?gx)) (com ?qz41&:(and (> ?qz41 ?vmin2) (< ?qz41 ?gy))) (layer ~?lay2) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx)) (max ?gx) (com ?qz39&:(and (> ?qz39 ?vmin2) (< ?qz39 ?gy))) (max-net nil)))
  (not (horizontal (net-name ?nn) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qz40&:(and (> ?qz40 ?vmin2) (< ?qz40 ?gy)))))
  (horizontal (net-name ~?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?vmin2) (layer ~?lay3))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx)) (max ?qw89&:(> ?qw89 ?gx)) (com ?qz42&:(and (> ?qz42 ?gy) (< ?qz42 ?vmax2)))))
  (not (horizontal (net-name nil) (min ?gx) (max ?qw90&:(> ?qw90 ?gx)) (com ?qz43&:(and (> ?qz43 ?gy) (< ?qz43 ?vmax2))) (layer ~?lay2) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz23&:(< ?qz23 ?gx)) (max ?gx) (com ?qz44&:(and (> ?qz44 ?gy) (< ?qz44 ?vmax2))) (max-net nil)))
  (not (horizontal (net-name ?nn) (min ?qw32&:(<= ?qw32 ?gx)) (max ?qw46&:(>= ?qw46 ?gx)) (com ?qz45&:(and (> ?qz45 ?gy) (< ?qz45 ?vmax2)))))
  (horizontal (net-name ~?nn&~nil) (min ?qw33&:(<= ?qw33 ?gx)) (max ?qw47&:(>= ?qw47 ?gx)) (com ?vmax2) (layer ~?lay4))
  ?h1 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) (max ?hmax&:(and (>= ?hmax ?vcom) (> ?hmax ?hmin))) (com ?gy) (layer ?lay5&~?lay2) (compo ?hcpo) (commo ?hcmo) (max-net ?mn))
  =>
  (assert (horizontal (layer ?lay5) (com ?gy) (commo ?hcmo) (compo ?hcpo) (min-net ?nn) (max-net ?mn) (min ?vcom) (max ?hmax)))
  (modify ?h1 (max ?gx) (max-net ?nn))
  (assert (horizontal (layer ?lay5) (com ?gy) (commo ?hcmo) (compo ?hcpo) (net-name ?nn) (pin-name ?pn) (min ?gx) (max ?vcom)))
  (modify ?ff1 (grid-x ?vcom) (grid-layer ?lay5) (can-chng-layer no))
)


(defrule p180
  (context (present propagate-constraint))
  (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?vmax) (com ?vcom) (layer ?lay1) (compo ?cpo) (commo ?cmo))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb2&:(<= ?garb2 ?vcom)) (max ?garb3&:(>= ?garb3 ?vcom)) (com ?vmax) (layer ?garb4&~?lay1))
  (vertical (net-name ?nn1&~?nn&~nil) (min ?vmin&:(> ?vmin ?vmax)) (max ?garb5) (com ?vcom) (layer ?lay2))
  (horizontal (net-name ?nn1&~nil) (min ?garb6&:(<= ?garb6 ?vcom)) (max ?garb7&:(>= ?garb7 ?vcom)) (com ?vmin) (layer ?garb8&~?lay2))
  (vertical (net-name ~?nn&~nil) (min ?vmin2&:(<= ?vmin2 ?vmax)) (max ?vmax2&:(>= ?vmax2 ?vmin)) (com ?cpo) (layer ?lay3))
  ?v1 <- (vertical (net-name nil) (min ?vmax) (max ?vmax3&:(> ?vmax3 ?vmax)) (com ?vcom) (layer ?lay3))
  (not (vertical (net-name nil) (min ?vmax3) (com ?vcom)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw86&:(> ?qw86 ?vcom)) (com ?qz30&:(and (> ?qz30 ?vmax) (<= ?qz30 ?vmax3)))))
  (not (horizontal (net-name nil) (min ?vcom) (max ?qw89&:(> ?qw89 ?vcom)) (com ?qz31&:(and (> ?qz31 ?vmax) (<= ?qz31 ?vmax3))) (layer ~?lay3) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?vcom)) (max ?vcom) (com ?qz32&:(and (> ?qz32 ?vmax) (<= ?qz32 ?vmax3))) (max-net nil)))
  (not (horizontal (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz33&:(and (> ?qz33 ?vmax) (<= ?qz33 ?vmax3)))))
  =>
  (modify ?v1 (min =(+ ?vmax 1)) (min-net nil))
)

(defrule p181
  (context (present propagate-constraint))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) (pin-name ?pn))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx)) (grid-y ?garb1) (grid-layer ?lay) (pin-name ~?pn))
  (not (vertical (com ?qz38&:(and (> ?qz38 ?gx) (< ?qz38 ?gx2)))))
  ?h1 <- (horizontal (net-name nil) (min ?gx) (max ?egarb1&:(> ?egarb1 ?gx)) (com ?gy) (layer ?lay) (compo ?hcpo) (commo ?hcmo))
  (not (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx) (layer ?lay)))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw63&:(>= ?qw63 ?gx2)) (com ?gy)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx2)))
  =>
  (modify ?h1 (min =(+ ?gx 1)) (min-net ?nn))
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (min ?gx) (com ?gy) (max =(+ ?gx 1)) (commo ?hcmo) (compo ?hcpo) (layer ?lay)))
  (modify ?ff1 (grid-x =(+ ?gx 1)) (came-from west))
)

(defrule p182
  (context (present propagate-constraint))
  ?v3 <- (vertical (net-name nil) (min ?vmin2) (max ?vmax2&:(> ?vmax2 ?vmin2)) (com ?vcom) (layer ?garb1) (compo ?vcpo) (commo ?egarb1))
  (congestion (direction row) (coordinate ?vmax2) (como ?vmin2))
  (not (horizontal (status nil) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz64&:(and (<= ?qz64 ?vmax2) (>= ?qz64 ?vmin2)))))
  (not (vertical (status nil) (min ?vmax2) (max ?qw86&:(> ?qw86 ?vmax2)) (com ?vcom)))
  (not (vertical (status nil) (max ?vmin2) (min ?qz21&:(< ?qz21 ?vmin2)) (com ?vcom)))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?vmin2)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?vmax2))) (com ?vcpo) (layer ?lay))
  (horizontal (net-name nil) (min ?vcom) (max ?egarb2&:(> ?egarb2 ?vcom)) (com ?vmax2) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  (horizontal (net-name nil) (min ?vcom) (max ?egarb5&:(> ?egarb5 ?vcom)) (com ?vmin2) (layer ?lay) (compo ?egarb6) (commo ?egarb7))
  =>
  (retract ?v3)
)

(defrule p183
  (context (present propagate-constraint))
  ?v3 <- (vertical (net-name nil) (min ?vmin2) (max ?vmax2&:(> ?vmax2 ?vmin2)) (com ?vcom) (layer ?garb1) (compo ?garb2) (commo ?vcmo))
  (congestion (direction row) (coordinate ?vmax2) (como ?vmin2))
  (not (horizontal (status nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw86&:(> ?qw86 ?vcom)) (com ?qz64&:(and (<= ?qz64 ?vmax2) (>= ?qz64 ?vmin2)))))
  (not (vertical (status nil) (min ?vmax2) (max ?qw87&:(> ?qw87 ?vmax2)) (com ?vcom)))
  (not (vertical (status nil) (max ?vmin2) (min ?qz20&:(< ?qz20 ?vmin2)) (com ?vcom)))
  (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?vmin2)) (max ?qz65&:(and (> ?qz65 ?vmin) (>= ?qz65 ?vmax2))) (com ?vcmo) (layer ?lay))
  (horizontal (net-name nil) (min ?egarb1) (max ?vcom&:(> ?vcom ?egarb1)) (com ?vmax2) (layer ?lay) (compo ?egarb2) (commo ?egarb3))
  (horizontal (net-name nil) (min ?egarb6) (max ?vcom&:(> ?vcom ?egarb6)) (com ?vmin2) (layer ?lay) (compo ?egarb4) (commo ?egarb5))
  =>
  (retract ?v3)
)

(defrule p184
  (context (present propagate-constraint))
  ?h3 <- (horizontal (net-name nil) (min ?hmin2) (max ?hmax2&:(> ?hmax2 ?hmin2)) (com ?hcom) (layer ?garb1) (compo ?hcpo) (commo ?egarb1))
  (congestion (direction col) (coordinate ?hmax2) (como ?hmin2))
  (not (vertical (status nil) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?qz64&:(and (<= ?qz64 ?hmax2) (>= ?qz64 ?hmin2)))))
  (not (horizontal (status nil) (min ?hmax2) (max ?qw86&:(> ?qw86 ?hmax2)) (com ?hcom)))
  (not (horizontal (status nil) (max ?hmin2) (min ?qz21&:(< ?qz21 ?hmin2)) (com ?hcom)))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?hmin2)) (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?hmax2))) (com ?hcpo) (layer ?lay))
  (vertical (net-name nil) (min ?hcom) (max ?egarb2&:(> ?egarb2 ?hcom)) (com ?hmax2) (layer ?lay) (compo ?egarb3) (commo ?egarb4))
  (vertical (net-name nil) (min ?hcom) (max ?egarb5&:(> ?egarb5 ?hcom)) (com ?hmin2) (layer ?lay) (compo ?egarb6) (commo ?egarb7))
  =>
  (retract ?h3)
)

(defrule p185
  (context (present propagate-constraint))
  ?h3 <- (horizontal (net-name nil) (min ?hmin2) 
                     (max ?hmax2&:(> ?hmax2 ?hmin2)) (com ?hcom) (layer ?garb1)
                     (compo ?garb2) (commo ?hcmo))
  (congestion (direction col) (coordinate ?hmax2) (como ?hmin2))
  (not (vertical (status nil) (min ?qw74&:(<= ?qw74 ?hcom)) 
                 (max ?qw86&:(> ?qw86 ?hcom)) 
                 (com ?qz64&:(and (<= ?qz64 ?hmax2) (>= ?qz64 ?hmin2)))))
  (not (horizontal (status nil) (min ?hmax2) 
                   (max ?qw87&:(> ?qw87 ?hmax2)) (com ?hcom)))
  (not (horizontal (status nil) (max ?hmin2) 
                   (min ?qz20&:(< ?qz20 ?hmin2)) (com ?hcom)))
  (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?hmin2))
              (max ?qz65&:(and (> ?qz65 ?hmin) (>= ?qz65 ?hmax2))) 
              (com ?hcmo) (layer ?lay))
  (vertical (net-name nil) (min ?egarb1) (max ?hcom&:(> ?hcom ?egarb1))
            (com ?hmax2) (layer ?lay) (compo ?egarb3))
  (vertical (net-name nil) (min ?egarb5) (max ?hcom&:(> ?hcom ?egarb5)) 
            (com ?hmin2) (layer ?lay) (compo ?egarb6) (commo ?egarb7))
  =>
  (retract ?h3)
)

(defrule p538
  (finally-routed ?nn)
  ?i1 <- (included ?nn ?)
  =>
  (retract ?i1)
)

(defrule p539
  (finally-routed ?nn)
  ?ff1 <- (ff (net-name ?nn))
  =>
  (retract ?ff1)
)

(defrule p540
  (finally-routed ?nn)
  ?c1 <-  (constraint (constraint-type vertical | horizontal) (net-name-1 ?nn))
  =>
  (retract ?c1)
)

(defrule p541
  (finally-routed ?nn)
  ?c1 <-  (constraint (constraint-type vertical | horizontal) (net-name-2 ?nn))
  =>
  (retract ?c1)
)

(defrule p542
  (finally-routed ?nn)
  ?c <- (horizontal-cycle | vertical-cycle ?nn $?)
  =>
  (retract ?c)
)

(defrule p543
  (finally-routed ?nn)
  ?c1 <-  (constraint (constraint-type vertical | horizontal) (net-name-2 ?nn))
  =>
  (retract ?c1)
)

(defrule p544
  (finally-routed ?nn)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax) (com ?vcom) (layer ?lay) (compo ?garb1) (commo ?garb2))
  ?h1 <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?vcom)) (max ?hmax&:(and (> ?hmax ?hmin) (>= ?hmax ?vcom))) (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (layer ?lay) (compo ?hcpo) (commo ?hcmo) (min-net ?nn1))
  =>
  (assert (horizontal (min ?hmin) (max =(- ?vcom 1)) (com ?hcom) (compo ?hcpo) (commo ?hcmo) (layer ?lay) (min-net ?nn1)))
  (modify ?h1 (min =(+ ?vcom 1)) (min-net nil))
)

(defrule p545
  (finally-routed ?nn)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?lay) (compo ?garb1) (commo ?garb2))
  ?v1 <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?hcom)) (max ?vmax&:(and (> ?vmax ?vmin) (>= ?vmax ?hcom))) (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))) (layer ?lay) (compo ?vcpo) (commo ?vcmo) (min-net ?nn1))
  =>
  (assert (vertical (min ?vmin) (max =(- ?hcom 1)) (com ?vcom) (compo ?vcpo) (commo ?vcmo) (layer ?lay) (min-net ?nn1)))
  (modify ?v1 (min =(+ ?hcom 1)) (min-net nil))
)

(defrule p546
  (finally-routed ?nn)
  (vertical (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?rmax) (com ?com) (layer ?lay) (compo ?garb4) (commo ?garb3))
  ?v1 <- (vertical (net-name nil) (min ?rmax) (max ?garb2&:(> ?garb2 ?rmax)) (com ?com) (layer ?lay) (compo ?garb6) (commo ?garb5))
  =>
  (modify ?v1 (min =(+ ?rmax 1)) (min-net nil))
)

(defrule p547
  (finally-routed ?nn)
  (vertical (status nil) (net-name ?nn&~nil) (min ?rmin) (max ?garb1) (com ?com) (layer ?lay) (compo ?garb3) (commo ?garb4))
  ?v1 <- (vertical (net-name nil) (min ?garb2) (max ?rmin&:(> ?rmin ?garb2)) (com ?com) (layer ?lay) (compo ?garb5) (commo ?garb6))
  =>
  (modify ?v1 (max =(- ?rmin 1)) (max-net nil))
)

(defrule p548
  (finally-routed ?nn)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?garb1) (max ?rmax) (com ?com) (layer ?lay) (compo ?garb3) (commo ?garb4))
  ?h1 <- (horizontal (net-name nil) (min ?rmax) (max ?garb2&:(> ?garb2 ?rmax)) (com ?com) (layer ?lay) (compo ?garb5) (commo ?garb6))
  =>
  (modify ?h1 (min =(+ ?rmax 1)) (min-net nil))
)

(defrule p549
  (finally-routed ?nn)
  (horizontal (status nil) (net-name ?nn&~nil) (min ?rmin) (max ?garb1) (com ?com) (layer ?lay) (compo ?garb3) (commo ?garb4))
  ?h1 <- (horizontal (net-name nil) (min ?garb2) (max ?rmin&:(> ?rmin ?garb2)) (com ?com) (layer ?lay) (compo ?garb5) (commo ?garb6))
  =>
  (modify ?h1 (max =(- ?rmin 1)) (max-net nil))
)

(defrule p550
  ?f1 <- (finally-routed ?nn)
  =>
  (retract ?f1)
  (assert (context (present remove-routed-net-segments) (previous ?nn)))
)


(defrule p551
  (context (present remove-routed-net-segments) (previous ?nn))
  ?h1 <- (horizontal (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) 
                     (com ?c) (layer ?lay) (compo ?garb1) (commo ?garb2))
  =>
  (modify ?h1 (status routed))
  (printout t "hor " ?nn " " ?min " " ?max " " ?c " " ?lay crlf)
)

(defrule p552
  (context (present remove-routed-net-segments) (previous ?nn))
  ?v1 <- (vertical (status nil) (net-name ?nn&~nil) (min ?min) (max ?max) (com ?c) (layer ?lay) (compo ?garb1) (commo ?garb2))
  =>
  (modify ?v1 (status routed))
  (printout t "ver " ?nn " " ?min " " ?max " " ?c " " ?lay crlf)
)

(defrule p553
  (context (present remove-routed-net-segments) (previous ?nn))
  ?h <- (horizontal-s (net-name ?nn))
  =>
  (retract ?h)
)

(defrule p554
  (context (present remove-routed-net-segments) (previous ?nn))
  ?v <- (vertical-s (net-name ?nn))
  =>
  (retract ?v)
)

(defrule p555
  (context (present remove-routed-net-segments) (previous ?nn))
  ?p <- (pin (net-name ?nn))
  =>
  (retract ?p)
)

(defrule p556
  (context (present remove-routed-net-segments) (previous ?nn))
  ?n <- (net (net-name ?nn))
  =>
  (retract ?n)
)

(defrule p557
  ?c1 <-  (context (present remove-routed-net-segments))
  =>
  (retract ?c1)
  (assert (context (present check-for-routed-net)))
)



(defrule p32
  (context (present propagate-constraint))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes) (no-of-top-pins 1) (no-of-bottom-pins 1))
  (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay) (pin-name ?pn1))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?lay) (pin-name ?pn2))
  ?v <- (vertical (net-name nil) (min ?gy2) (max ?qw86&:(> ?qw86 ?gy2)) (com ?gx2) (layer ?lay) (commo ?cmo) (compo ?gx1) (min-net ?garb2))
  (not (horizontal (net-name nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw62&:(>= ?qw62 ?gx1)) (com ?gy2) (layer ?lay)))
  (congestion (direction row) (coordinate ?gy3) (como ?gy2))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy3)) (max ?qw63&:(>= ?qw63 ?gy3)) (com ?gx2) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw38&:(<= ?qw38 ?gx2)) (max ?qw64&:(>= ?qw64 ?gx2)) (com ?gy3) (layer ?lay)))
  =>
  (modify ?v (min ?gy3) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn2) (min ?gy2) (max ?gy3) (com ?gx2) (compo ?gx1) (commo ?cmo) (layer ?lay)))
  (modify ?ff (grid-y ?gy3) (can-chng-layer nil) (came-from south))
)

(defrule p33
  (context (present propagate-constraint))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes) (no-of-top-pins 1) (no-of-bottom-pins 1))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay) (pin-name ?pn1))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?lay) (pin-name ?pn2))
  ?v <- (vertical (net-name nil) (min ?qz20&:(< ?qz20 ?gy1)) (max ?gy1) (com ?gx1) (layer ?lay) (commo ?gx2) (compo ?cpo) (min-net ?garb2))
  (not (horizontal (net-name nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw62&:(>= ?qw62 ?gx1)) (com ?gy1) (layer ?lay)))
  (congestion (direction row) (coordinate ?gy1) (como ?gy3))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy3)) (max ?qw63&:(>= ?qw63 ?gy3)) (com ?gx1) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw64&:(>= ?qw64 ?gx1)) (com ?gy3) (layer ?lay)))
  =>
  (modify ?v (max ?gy3) (max-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn1) (min ?gy3) (max ?gy1) (com ?gx1) (compo ?cpo) (commo ?gx2) (layer ?lay)))
  (modify ?ff (grid-y ?gy3) (can-chng-layer nil) (came-from north))
)

(defrule p34
  (context (present propagate-constraint))
  (net (net-name ?nn) (net-no-of-pins 2) (net-is-routed ~yes) (no-of-top-pins 1) (no-of-bottom-pins 1))
  (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay) (pin-name ?pn1))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy2&:(< ?gy2 ?gy1)) (grid-layer ?lay) (pin-name ?pn2))
  ?v <- (vertical (net-name nil) (min ?gy2) (max ?qw86&:(> ?qw86 ?gy2)) (com ?gx2) (layer ?lay) (commo ?gx1) (compo ?cpo) (min-net ?garb2))
  (not (horizontal (net-name nil) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw62&:(>= ?qw62 ?gx2)) (com ?gy2) (layer ?lay)))
  (congestion (direction row) (coordinate ?gy3) (como ?gy2))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?gy3)) (max ?qw63&:(>= ?qw63 ?gy3)) (com ?gx2) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw37&:(<= ?qw37 ?gx2)) (max ?qw64&:(>= ?qw64 ?gx2)) (com ?gy3) (layer ?lay)))
  =>
  (modify ?v (min ?gy3) (min-net ?nn))
  (assert (vertical (net-name ?nn) (pin-name ?pn2) (min ?gy2) (max ?gy3) (com ?gx2) (compo ?cpo) (commo ?gx1) (layer ?lay)))
  (modify ?ff (grid-y ?gy3) (can-chng-layer nil) (came-from south))
)

