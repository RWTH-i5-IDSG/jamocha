(defrule p80
  (context (present propagate-constraint))
  (last-col ?lc)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin&:(<= ?vmin 1))
            (max ?vmax) (com ?vcom) (layer ?vlay) (pin-name ?pn) (commo ?vcmo))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?vcom) (max ?hmax&:(>= ?hmax ?lc)) 
              (com ?vmax) (layer ?hlay))
  ?h <- (horizontal (net-name nil) (min ?hmin&:(< ?hmin ?vcom)) (max ?vcom)
                    (compo ?qt1) (commo ?qt2) (status ?qt3) (min-net ?qt4) (max-net ?qt5)
                    (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (layer ?lay))
  (not (horizontal (net-name ?nn) (min ?qz20&:(< ?qz20 ?vcom)) 
                   (max ?qw62&:(>= ?qw62 ?vcom)) 
                   (com ?qz30&:(and (> ?qz30 ?hcom) (<= ?qz30 ?vmax)))))
  (not (horizontal (net-name ?nn) (min ?qz21&:(< ?qz21 ?vcom)) 
                   (max ?qw63&:(>= ?qw63 ?vcom))
                   (com ?qz47&:(and (< ?qz47 ?hcom) (>= ?qz47 ?vmin)))))
  (not (horizontal (net-name nil) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?qz31&:(and (> ?qz31 ?hcom) (<= ?qz31 ?vmax)))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw65&:(>= ?qw65 ?vcom)) (com ?qz48&:(and (< ?qz48 ?hcom) (>= ?qz48 ?vmin)))))
  (not (horizontal (net-name ?nn) (min ?qz22&:(< ?qz22 ?vcom)) (max ?qw66&:(>= ?qw66 ?vcom)) (com ?hcom) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?vcom)) (max ?qw67&:(>= ?qw67 ?vcom)) (com ?hcom) (layer ~?lay)))
  (not (vertical (net-name ?nn) (min ?qw77&:(<= ?qw77 ?vmax)) (max ?qw68&:(>= ?qw68 ?vmax)) (com ?qz32&:(and (> ?qz32 ?vcom) (<= ?qz32 ?hmax)))))
  (not (vertical (net-name nil) (min ?qw78&:(<= ?qw78 ?vmax)) (max ?qw69&:(>= ?qw69 ?vmax)) (com ?qz33&:(and (> ?qz33 ?vcom) (<= ?qz33 ?hmax)))))
  (not (vertical (net-name nil) (min ?qw79&:(<= ?qw79 ?vmax)) (max ?qw70&:(>= ?qw70 ?vmax)) (com ?vcom)))
  (not (vertical (net-name ?nn) (min ?vmax) (max ?qw86&:(> ?qw86 ?vmax)) (com ?vcom)))
  (not (vertical (net-name nil) (min ?qw80&:(<= ?qw80 ?vmin)) (max ?qw71&:(>= ?qw71 ?vmin)) (com ?vcom)))
  (not (vertical (net-name ?nn) (min ?qz23&:(< ?qz23 ?vmin)) (max ?vmin) (com ?vcom)))
  =>
  (assert (horizontal (com ?hcom) (compo ?qt1) (commo ?qt2) (layer ?lay) 
                      (status ?qt3) (min-net ?qt4) 
                      (max ?vcom) (max-net ?qt5)
                      (net-name ?nn) (pin-name ?pn) (min ?vcmo)))
  (modify ?h (max ?vcmo) (max-net ?nn))
  (assert (ff (grid-x ?vcmo) (grid-y ?hcom) (grid-layer ?lay) (net-name ?nn) (pin-name ?pn) (came-from east)))
)

(defrule p81
  (context (present propagate-constraint))
  (last-row ?lr)
  (last-col ?lc)
  (vertical (status nil) (net-name ?nn&~nil) (min ?vmin) (max ?vmax&:(>= ?vmax ?lr)) (com ?vcom) (layer ?vlay) (pin-name ?pn) (commo ?vcmo))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?vcom) (max ?hmax&:(>= ?hmax ?lc)) (com ?vmin) (layer ?hlay))
  ?h <- (horizontal (net-name nil) (min ?hmin&:(< ?hmin ?vcom)) (max ?vcom) 
                    (com ?hcom&:(and (>= ?hcom ?vmin) (<= ?hcom ?vmax))) (layer ?lay)
                    (compo ?xc1) (commo ?xc2) (status ?xc3)
                    (min-net ?xc4) (max-net ?xc5))
  (not (horizontal (net-name ?nn) (min ?qz20&:(< ?qz20 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?qz30&:(and (> ?qz30 ?hcom) (<= ?qz30 ?vmax)))))
  (not (horizontal (net-name ?nn) (min ?qz21&:(< ?qz21 ?vcom)) (max ?qw63&:(>= ?qw63 ?vcom)) (com ?qz47&:(and (< ?qz47 ?hcom) (>= ?qz47 ?vmin)))))
  (not (horizontal (net-name nil) (min ?qw75&:(<= ?qw75 ?vcom)) (max ?qw64&:(>= ?qw64 ?vcom)) (com ?qz31&:(and (> ?qz31 ?hcom) (<= ?qz31 ?vmax)))))
  (not (horizontal (net-name nil) (min ?qw76&:(<= ?qw76 ?vcom)) (max ?qw65&:(>= ?qw65 ?vcom)) (com ?qz48&:(and (< ?qz48 ?hcom) (>= ?qz48 ?vmin)))))
  (not (horizontal (net-name ?nn) (min ?qz22&:(< ?qz22 ?vcom)) (max ?qw66&:(>= ?qw66 ?vcom)) (com ?hcom) (layer ~?lay)))
  (not (horizontal (net-name nil) (min ?qw77&:(<= ?qw77 ?vcom)) (max ?qw67&:(>= ?qw67 ?vcom)) (com ?hcom) (layer ~?lay)))
  (not (vertical (net-name ?nn) (min ?qw78&:(<= ?qw78 ?vmin)) (max ?qw68&:(>= ?qw68 ?vmin)) (com ?qz32&:(and (> ?qz32 ?vcom) (<= ?qz32 ?hmax)))))
  (not (vertical (net-name nil) (min ?qw79&:(<= ?qw79 ?vmin)) (max ?qw69&:(>= ?qw69 ?vmin)) (com ?qz33&:(and (> ?qz33 ?vcom) (<= ?qz33 ?hmax)))))
  (not (vertical (net-name nil) (min ?qw80&:(<= ?qw80 ?vmin)) (max ?qw70&:(>= ?qw70 ?vmin)) (com ?vcom)))
  (not (vertical (net-name ?nn) (min ?qz23&:(< ?qz23 ?vmin)) (max ?vmin) (com ?vcom)))
  (not (vertical (net-name nil) (min ?qw81&:(<= ?qw81 ?vmax)) (max ?qw71&:(>= ?qw71 ?vmax)) (com ?vcom)))
  (not (vertical (net-name ?nn) (min ?vmax) (max ?qw86&:(> ?qw86 ?vmax)) (com ?vcom)))
  =>
  (assert (horizontal (net-name ?nn) (pin-name ?pn) (min ?vcmo) (layer ?lay)
                      (max ?vcom) (com ?hcom) (compo ?xc1) (commo ?xc2) (status ?xc3)
                      (min-net ?xc4) (max-net ?xc5)))
  (modify ?h (max ?vcmo) (max-net ?nn))
  (assert (ff (grid-x ?vcmo) (grid-y ?hcom) (grid-layer ?lay) (net-name ?nn) (pin-name ?pn) (came-from east)))
)

(defrule p82
  (context (present propagate-constraint))
  (horizontal (net-name ?nn&~nil) (min ?hmin) (max ?hmax) (com ?hcom) (layer ?hlay) (pin-name ?pn) (commo ?hcmo))
  (vertical (status nil) (net-name ?nn&~nil) (min ?hcom) (max ?vmax1) (com ?hmin) (layer ?vlay1))
  (vertical (status nil) (net-name ?nn&~nil) (min ?hcom) (max ?vmax2) (com ?hmax) (layer ?vlay2))
  ?v <- (vertical (net-name nil) (min ?vmin&:(< ?vmin ?hcom)) (max ?hcom) 
                  (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))) (layer ?lay)
                  (compo ?xc1) (commo ?xc2) (status ?xc3)
                  (min-net ?xc4) (max-net ?xc5))
  (not (vertical (net-name ?nn) (min ?qz20&:(< ?qz20 ?hcom)) (max ?qw62&:(>= ?qw62 ?hcom)) (com ?qz30&:(and (> ?qz30 ?vcom) (<= ?qz30 ?hmax)))))
  (not (vertical (net-name ?nn) (min ?qz21&:(< ?qz21 ?hcom)) (max ?qw63&:(>= ?qw63 ?hcom)) (com ?qz47&:(and (< ?qz47 ?vcom) (>= ?qz47 ?hmin)))))
  (not (vertical (net-name nil) (min ?qw74&:(<= ?qw74 ?hcom)) (max ?qw64&:(>= ?qw64 ?hcom)) (com ?qz31&:(and (> ?qz31 ?vcom) (<= ?qz31 ?hmax)))))
  (not (vertical (net-name nil) (min ?qw76&:(<= ?qw76 ?hcom)) (max ?qw65&:(>= ?qw65 ?hcom)) (com ?qz48&:(and (< ?qz48 ?vcom) (>= ?qz48 ?hmin)))))
  (not (vertical (net-name ?nn) (min ?qz22&:(< ?qz22 ?hcom)) (max ?qw66&:(>= ?qw66 ?hcom)) (com ?vcom) (layer ~?lay)))
  (not (vertical (net-name nil) (min ?qw77&:(<= ?qw77 ?hcom)) (max ?qw67&:(>= ?qw67 ?hcom)) (com ?vcom) (layer ~?lay)))
  (not (horizontal (net-name ?nn) (min ?qw78&:(<= ?qw78 ?hmax)) (max ?qw68&:(>= ?qw68 ?hmax)) (com ?qz32&:(and (> ?qz32 ?hcom) (<= ?qz32 ?vmax2)))))
  (not (horizontal (net-name ?nn) (min ?qw79&:(<= ?qw79 ?hmin)) (max ?qw69&:(>= ?qw69 ?hmin)) (com ?qz33&:(and (> ?qz33 ?hcom) (<= ?qz33 ?vmax1)))))
  (not (horizontal (net-name nil) (min ?qw80&:(<= ?qw80 ?hmax)) (max ?qw70&:(>= ?qw70 ?hmax)) (com ?qz34&:(and (> ?qz34 ?hcom) (<= ?qz34 ?vmax2)))))
  (not (horizontal (net-name nil) (min ?qw81&:(<= ?qw81 ?hmin)) (max ?qw71&:(>= ?qw71 ?hmin)) (com ?qz35&:(and (> ?qz35 ?hcom) (<= ?qz35 ?vmax1)))))
  (not (horizontal (net-name nil) (min ?qw82&:(<= ?qw82 ?hmax)) (max ?qw72&:(>= ?qw72 ?hmax)) (com ?hcom)))
  (not (horizontal (net-name ?nn) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) (com ?hcom)))
  (not (horizontal (net-name nil) (min ?qw83&:(<= ?qw83 ?hmin)) (max ?qw73&:(>= ?qw73 ?hmin)) (com ?hcom)))
  (not (horizontal (net-name ?nn) (min ?qz23&:(< ?qz23 ?hmin)) (max ?hmin) (com ?hcom)))
  =>
  (assert (vertical (net-name ?nn) (pin-name ?pn) (min ?hcmo) (layer ?lay)
                    (max ?hcom) (com ?vcom) (compo ?xc1) (commo ?xc2) (status ?xc3)
                    (min-net ?xc4) (max-net ?xc5)))
  (modify ?v (max ?hcmo) (max-net ?nn))
  (assert (ff (grid-x ?vcom) (grid-y ?hcmo) (grid-layer ?lay) (net-name ?nn) (pin-name ?pn) (came-from north)))
)

(defrule p377
  (context (present find-no-of-pins-on-a-row-col))
  (horizontal (net-name nil) (min ?min) (max ?max&:(> ?max ?min)) (com ?gy) (layer ?lay) (compo ?egarb1) (commo ?egabr2))
  (ff (net-name ?nn) (grid-x ?fx&:(and (>= ?fx ?min) (<= ?fx ?max))) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn1))
  (ff (net-name ?nn) (grid-y ?gy) (pin-name ?pn2&~?pn1) (grid-x ?gx&:(and (> ?gx ?fx) (<= ?gx ?max))))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?fx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?qw60&:(and (>= ?qw60 ?fx) (<= ?qw60 ?gx))) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?fx)) (max ?qw63&:(>= ?qw63 ?fx)) (com ?gy) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy) (layer ?lay)))
  (net (net-name ?nn) (net-no-of-pins ?np))
  (not (total (net-name ?nn) (row-col row) (coor ?gy)))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?gy) (level-pins 1) (total-pins ?np) (min-xy ?fx) (max-xy ?gx) (last-pin ?pn1) (last-xy ?fx)))
)

(defrule p378
  (context (present find-no-of-pins-on-a-row-col))
  (horizontal (net-name nil) (min ?min) (max ?max&:(> ?max ?min)) (com ?gy) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (ff (net-name ?nn) (grid-x ?fx&:(and (>= ?fx ?min) (<= ?fx ?max))) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn1))
  (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx&:(and (> ?gx ?fx) (<= ?gx ?max))) (pin-name ?pn2&~?pn1))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (pin-name ~?pn1)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?fx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?qw60&:(and (>= ?qw60 ?fx) (<= ?qw60 ?gx))) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?fx)) (max ?qw64&:(>= ?qw64 ?fx)) (com ?gy) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?gy) (layer ?lay)))
  (net (net-name ?nn) (net-no-of-pins ?np))
  (not (total (net-name ?nn) (row-col row) (coor ?gy)))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?gy) (level-pins 1) (total-pins ?np) (min-xy ?fx) (max-xy ?gx) (last-pin ?pn1) (last-xy ?fx)))
)

(defrule p379
  (context (present find-no-of-pins-on-a-row-col))
  (horizontal (net-name nil) (min ?min) (max ?max&:(> ?max ?min)) (com ?gy) (layer ?lay))
  (ff (net-name ?nn) (grid-x ?fx&:(and (>= ?fx ?min) (<= ?fx ?max))) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn1))
  (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw62&:(>= ?qw62 ?gy)) (com ?gx&:(and (< ?gx ?fx) (>= ?gx ?min))) (pin-name ?pn2&~?pn1))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (pin-name ~?pn1)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw63&:(>= ?qw63 ?fx)) (com ?gy)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?qz64&:(and (<= ?qz64 ?fx) (>= ?qz64 ?gx))) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?fx)) (max ?qw65&:(>= ?qw65 ?fx)) (com ?gy) (layer ?lay)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy) (layer ?lay)))
  (net (net-name ?nn) (net-no-of-pins ?np))
  (not (total (net-name ?nn) (row-col row) (coor ?gy)))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?gy) (level-pins 1) (total-pins ?np) (min-xy ?gx) (max-xy ?fx) (last-pin ?pn2) (last-xy ?gx)))
)

(defrule p380
  (context (present find-no-of-pins-on-a-row-col))
  (vertical (net-name nil) (min ?min) (max ?max&:(> ?max ?min)) (com ?gx) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?fy&:(and (>= ?fy ?min) (<= ?fy ?max))) (grid-layer ?garb1) (pin-name ?pn1))
  (ff (net-name ?nn) (grid-x ?gx) (pin-name ?pn2&~?pn1) (grid-y ?gy&:(and (> ?gy ?fy) (<= ?gy ?max))))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw75&:(<= ?qw75 ?fy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?qw60&:(and (>= ?qw60 ?fy) (<= ?qw60 ?gy))) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?fy)) (max ?qw62&:(>= ?qw62 ?fy)) (com ?gx) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx) (layer ?lay)))
  (net (net-name ?nn) (net-no-of-pins ?np))
  (not (total (net-name ?nn) (row-col col) (coor ?gx)))
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?gx) (level-pins 1) (total-pins ?np) (min-xy ?fy) (max-xy ?gy) (last-pin ?pn1) (last-xy ?fy)))
)

(defrule p381
  (context (present find-no-of-pins-on-a-row-col))
  (vertical (net-name nil) (min ?min) (max ?max&:(> ?max ?min)) (com ?gx) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?fy&:(and (>= ?fy ?min) (<= ?fy ?max))) (grid-layer ?garb1) (pin-name ?pn1))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy&:(and (> ?gy ?fy) (<= ?gy ?max))) (pin-name ?pn2&~?pn1))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (pin-name ~?pn1)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw74&:(<= ?qw74 ?fy)) (max ?qw63&:(>= ?qw63 ?gy)) (com ?gx)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qw60&:(and (>= ?qw60 ?fy) (<= ?qw60 ?gy))) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw75&:(<= ?qw75 ?fy)) (max ?qw62&:(>= ?qw62 ?fy)) (com ?gx) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx) (layer ?lay)))
  (net (net-name ?nn) (net-no-of-pins ?np))
  (not (total (net-name ?nn) (row-col col) (coor ?gx)))
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?gx) (level-pins 1) (total-pins ?np) (min-xy ?fy) (max-xy ?gy) (last-pin ?pn1) (last-xy ?fy)))
)


(defrule p382
  (context (present find-no-of-pins-on-a-row-col))
  (vertical (net-name nil) (min ?min) (max ?max&:(> ?max ?min)) (com ?gx) (layer ?lay) (compo ?egarb1) (commo ?egarb2))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?fy&:(and (>= ?fy ?min) (<= ?fy ?max))) (grid-layer ?garb1) (pin-name ?pn1))
  (horizontal (status nil) (net-name ?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (com ?gy&:(and (< ?gy ?fy) (>= ?gy ?min))) (pin-name ?pn2&~?pn1))
  (not (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (pin-name ~?pn1)))
  (not (vertical (status nil) (net-name ?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?qw63&:(>= ?qw63 ?fy)) (com ?gx)))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw45&:(>= ?qw45 ?gx)) (com ?qw60&:(and (>= ?qw60 ?gy) (<= ?qw60 ?fy))) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?fy)) (max ?qw62&:(>= ?qw62 ?fy)) (com ?gx) (layer ?lay)))
  (not (vertical (net-name ~?nn&~nil) (min ?qw40&:(<= ?qw40 ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx) (layer ?lay)))
  (net (net-name ?nn) (net-no-of-pins ?np))
  (not (total (net-name ?nn) (row-col col) (coor ?gx)))
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?gx) (level-pins 1) (total-pins ?np) (min-xy ?gy) (max-xy ?fy) (last-pin ?pn2) (last-xy ?gy)))
)

(defrule p383
  (context (present find-no-of-pins-on-a-row-col))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?gy) (grid-layer ?garb2) (pin-name ?pn))
  (net (net-name ?nn) (net-no-of-pins ?np) (no-of-top-pins ?np) (no-of-bottom-pins ?garb3))
  (not (ff (net-name ?nn) (grid-y ?qw74&:(<= ?qw74 ?gy)) (pin-name ~?pn)))
  (not (total (net-name ?nn)))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?gy) (level-pins ?np) (total-pins ?np)))
)

(defrule p384
  (context (present find-no-of-pins-on-a-row-col))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?gy) (grid-layer ?garb2) (pin-name ?pn))
  (net (net-name ?nn) (net-no-of-pins ?np) (no-of-top-pins ?garb3) (no-of-bottom-pins ?np))
  (not (ff (net-name ?nn) (grid-y ?qw63&:(>= ?qw63 ?gy)) (pin-name ~?pn)))
  (not (total (net-name ?nn)))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?gy) (level-pins ?np) (total-pins ?np)))
)



(defrule p385
  (context (present find-no-of-pins-on-a-row-col))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn))
  (net (net-name ?nn) (net-no-of-pins ?np) (no-of-top-pins ?np) (no-of-bottom-pins ?garb2))
  (not (total (net-name ?nn)))
  (ff (net-name ?nn) (grid-y ?gy) (pin-name ~?pn) (grid-x ?fx&:(> ?fx ?gx)))
  (not (ff (net-name ?nn) (grid-y ?qz20&:(< ?qz20 ?gy))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw62&:(>= ?qw62 ?fx)) (com ?gy)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw29&:(<= ?qw29 ?gx)) (max ?qw63&:(>= ?qw63 ?fx)) (com ?gy)))
  ?v1 <- (vertical (net-name nil) (min ?min&:(< ?min ?gy)) (max ?qw64&:(>= ?qw64 ?gy)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (min-net ?mn))
  (not (horizontal (net-name ~nil&~?nn) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (layer ?lay) (compo ?gy)))
  (congestion (direction row) (coordinate ?gy) (como ?hcmo))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcmo)) (max ?qw65&:(>= ?qw65 ?hcmo)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?hcmo) (min ?min) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?mn) (max-net ?nn)))
  (modify ?v1 (min ?gy) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy) (min ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?ff1 (grid-y ?hcmo) (can-chng-layer nil))
  (assert (total (net-name ?nn) (row-col row) (coor ?hcmo) (level-pins ?np) (total-pins ?np)))
)

(defrule p386
  (context (present find-no-of-pins-on-a-row-col))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?garb1) (pin-name ?pn))
  (net (net-name ?nn) (net-no-of-pins ?np) (no-of-top-pins ?garb2) (no-of-bottom-pins ?np))
  (not (total (net-name ?nn)))
  (ff (net-name ?nn) (grid-y ?gy) (pin-name ~?pn) (grid-x ?fx&:(> ?fx ?gx)))
  (not (ff (net-name ?nn) (grid-y ?qw86&:(> ?qw86 ?gy))))
  (not (horizontal (net-name nil) (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw63&:(>= ?qw63 ?fx)) (com ?gy)))
  (not (horizontal (status nil) (net-name ?nn&~nil) (min ?qw31&:(<= ?qw31 ?gx)) (max ?qw62&:(>= ?qw62 ?fx)) (com ?gy)))
  ?v1 <- (vertical (net-name nil) (min ?qw39&:(<= ?qw39 ?gy)) (max ?max&:(> ?max ?gy)) (com ?gx) (layer ?lay) (commo ?cmo) (compo ?cpo) (max-net ?mn))
  (not (horizontal (net-name ~?nn&~nil) (min ?qw32&:(<= ?qw32 ?gx)) (max ?qw44&:(>= ?qw44 ?gx)) (layer ?lay) (compo ?gy)))
  (congestion (direction row) (coordinate ?hcmo) (como ?gy))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw74&:(<= ?qw74 ?hcmo)) (max ?qw64&:(>= ?qw64 ?hcmo)) (com ?gx) (layer ?lay)))
  =>
  (assert (vertical (net-name nil) (max ?max) (min ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (min-net ?nn) (max-net ?mn)))
  (modify ?v1 (max ?gy) (max-net ?nn))
  (assert (vertical (net-name ?nn) (min ?gy) (max ?hcmo) (layer ?lay) (com ?gx) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?ff1 (grid-y ?hcmo) (can-chng-layer nil))
  (assert (total (net-name ?nn) (row-col row) (coor ?hcmo) (level-pins ?np) (total-pins ?np)))
)

(defrule p387
  ?t1 <-  (total (net-name ?nn) (row-col row) (coor ?y) (level-pins ?np) (total-pins ?np) (min-xy nil) (max-xy nil))
  (pin (net-name ?nn) (pin-x ?px1))
  (not (pin (net-name ?nn) (pin-x ?qz20&:(< ?qz20 ?px1))))
  (pin (net-name ?nn) (pin-x ?px2&:(> ?px2 ?px1)))
  (not (pin (net-name ?nn) (pin-x ?qw86&:(> ?qw86 ?px2))))
  =>
  (modify ?t1 (min-xy ?px1) (max-xy ?px2))
)

(defrule p388
  ?f1 <- (context (present find-no-of-pins-on-a-row-col))
  =>
  (assert (context (previous find-no-of-pins-on-a-row-col)))
  (retract ?f1)
)

(defrule p389
  ?f1 <- (context (previous find-no-of-pins-on-a-row-col))
  (total)
  =>
  (retract ?f1)
  (assert (context (present modify-total)))
)

(defrule p390
  ?f1 <- (context (present delete-totals))
  =>
  (modify ?f1 (present choose-between-nets-on-the-same-row-col))
)

(defrule p391
  ?c1 <-  (context (present choose-between-nets-on-the-same-row-col | choose-between-nets-on-the-same-row-col-con))
  ?t1 <-  (total (net-name ?nn) (row-col ?rc&row | col) (coor ?xy) 
                 (level-pins ?cou) (total-pins ?garb1) (nets $?n1))
  (not (total (net-name ~?nn) (level-pins ?qw63&:(>= ?qw63 ?cou))))
  (not (total (net-name ?nn) (row-col ~?rc) (level-pins ?qw62&:(>= ?qw62 ?cou))))
  =>
  (retract ?c1 ?t1)
  (assert (change-priority))
  (assert (tran-total ?rc ?xy $?n1))
)

(defrule p392
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?garb1) (row-col ?garb2&row | col) (coor ?garb3) (level-pins ?cou) (total-pins ?cou))
  ?t1 <-  (total (level-pins ?qz20&:(< ?qz20 ?cou)))
  =>
  (retract ?t1)
)

(defrule p393
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?garb1) (row-col ?garb2&row | col) (coor ?garb3) (level-pins ?cou) (total-pins ?garb4))
  ?t1 <-  (total (level-pins ?cou1) (total-pins ?qv1&~?cou1&:(< ?qv1 ?cou)))
  =>
  (retract ?t1)
)

(defrule p394
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?nn) (row-col ?rc&row | col) (coor ?garb1) (level-pins ?cou))
  ?t1 <-  (total (net-name ?nn) (row-col ~?rc) (level-pins ?cou))
  =>
  (retract ?t1)
)

(defrule p395
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?nn) (row-col ?rc&row | col) (coor ?xy) (level-pins ?cou) (total-pins ?garb1))
  ?t1 <-  (total (net-name ?nn) (row-col ?rc) (coor ~?xy) (level-pins ?cou))
  =>
  (retract ?t1)
)

(defrule p396
  ?c1 <-  (context (present choose-between-nets-on-the-same-row-col | choose-between-nets-on-the-same-row-col-con))
  ?t1 <-  (total (net-name ?nn) (row-col ?rc&row | col) (coor ?xy) (level-pins ?cou)
                 (total-pins ?cou) (nets $?n1))
  (not (total (net-name ~?nn) (level-pins ?cou) (total-pins ?cou)))
  =>
  (retract ?c1 ?t1)
  (assert (change-priority))
  (assert (tran-total ?rc ?xy $?n1))
)

(defrule p397
  ?t1 <-  (tran-total ?rc ?xy ?nn&~nil&~end ?min ?max $?rest)
  =>
  (printout t crlf "***** next net to be routed is " ?nn " *****" crlf)
  (assert (next-segment ?nn ?rc ?xy useless ?min ?max))
  (retract ?t1)
  (assert (tran-total ?rc ?xy $?rest))
)

(defrule p398
  (or ?t1 <-  (tran-total ?rc ?xy nil)
      ?t1 <-  (tran-total ?rc ?xy))
  =>
  (assert (dominant-layer))
  (retract ?t1)
  (assert (goal cleanup total-verti))
  (assert (goal cleanup counted-verti))
  (assert (goal cleanup total))
  (assert (goal cleanup extend-ff-tried))
)


(defrule p399
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?nn) (row-col ?garb1&row | col) (coor ?garb2) (level-pins ?cou) (total-pins ?cou))
  ?t1 <-  (total (net-name ~?nn) (level-pins ?cou) (total-pins ?qw86&:(> ?qw86 ?cou)))
  =>
  (retract ?t1)
)

(defrule p400
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?nn) (row-col ?garb1&row | col) (coor ?garb2) (level-pins ?cou) (total-pins ?cou1))
  ?t1 <-  (total (net-name ~?nn) (level-pins ?cou) (total-pins ?qw86&:(> ?qw86 ?cou1)))
  (not (total (net-name ?garb3) (row-col ?garb4) (coor ?garb5) (level-pins ?cou2) (total-pins ?cou2)))
  =>
  (retract ?t1)
)

(defrule p401
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?garb1) (row-col ?garb2) (coor ?garb3) (level-pins ?cou1) (total-pins ?cou2))
  ?t1 <-  (total (net-name ?garb4) (row-col ?garb5) (coor ?garb6) (level-pins ?qz20&:(< ?qz20 ?cou1)) (total-pins ?qz21&:(< ?qz21 ?cou2)))
  =>
  (retract ?t1)
)

(defrule p402
  ?f1 <- (context (present choose-between-nets-on-the-same-row-col))
  =>
  (modify ?f1 (present choose-between-nets-on-the-same-row-col-con))
)

(defrule p403
  (context (present choose-between-nets-on-the-same-row-col-con))
  ?t1 <-  (total (net-name ?nn) (row-col row) (level-pins ?cou) (total-pins ?cou2) (cong nil))
  (total (net-name ~?nn) (level-pins ?cou) (total-pins ?cou2))
  (pin (net-name ?nn) (pin-x ?px1))
  (not (pin (net-name ?nn) (pin-x ?qz20&:(< ?qz20 ?px1))))
  (pin (net-name ?nn) (pin-x ?px2&:(> ?px2 ?px1)))
  (not (pin (net-name ?nn) (pin-x ?qw86&:(> ?qw86 ?px2))))
  (congestion (direction col) (coordinate ?qw60&:(and (>= ?qw60 ?px1) (<= ?qw60 ?px2))) (no-of-nets ?non))
  (not (congestion (direction col) (coordinate ?qw61&:(and (>= ?qw61 ?px1) (<= ?qw61 ?px2))) (no-of-nets ?qw87&:(> ?qw87 ?non))))
  =>
  (modify ?t1 (cong ?non))
)

(defrule p404
  (context (present choose-between-nets-on-the-same-row-col-con))
  ?t1 <-  (total (net-name ?nn) (row-col col) (level-pins ?cou) (total-pins ?cou2) (cong nil))
  (total (net-name ~?nn) (level-pins ?cou) (total-pins ?cou2))
  (pin (net-name ?nn) (pin-y ?py1))
  (not (pin (net-name ?nn) (pin-y ?qz20&:(< ?qz20 ?py1))))
  (pin (net-name ?nn) (pin-y ?py2&:(> ?py2 ?py1)))
  (not (pin (net-name ?nn) (pin-y ?qw86&:(> ?qw86 ?py2))))
  (congestion (direction row) (coordinate ?qw60&:(and (>= ?qw60 ?py1) (<= ?qw60 ?py2))) (no-of-nets ?non))
  (not (congestion (direction row) (coordinate ?qw61&:(and (>= ?qw61 ?py1) (<= ?qw61 ?py2))) (no-of-nets ?qw87&:(> ?qw87 ?non))))
  =>
  (modify ?t1 (cong ?non))
)

(defrule p405
  (context (present choose-between-nets-on-the-same-row-col-con))
  (total (net-name ?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil) (min-xy ?min1) (max-xy ?max1))
  (total (net-name ~?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil) (min-xy ?min2&:(> ?min2 ?max1)) (max-xy ?max2))
  ?t <- (total (net-name ?nn1) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil) (min-xy ?qw74&:(<= ?qw74 ?max1)) (max-xy ?qw67&:(>= ?qw67 ?min2)))
  =>
  (retract ?t)
)

(defrule p406
  (context (present choose-between-nets-on-the-same-row-col-con))
  (total (net-name ?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil) (min-xy ?min1) (max-xy ?max1))
  ?t <- (total (net-name ~?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil) (min-xy ?qw62&:(>= ?qw62 ?min1)) (max-xy ?qw75&:(<= ?qw75 ?max1)))
  =>
  (retract ?t)
)

(defrule p407
  (context (present choose-between-nets-on-the-same-row-col-con))
  (total (net-name ?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil))
  ?t <- (total (net-name ?nn1&~?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil))
  (vertical-cycle | horizontal-cycle ?nn1)
  =>
  (retract ?t)
)

(defrule p408
  (context (present choose-between-nets-on-the-same-row-col-con))
  (total (net-name ?nn) (row-col row) (coor ?y) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil))
  ?t <- (total (net-name ?nn1&~?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil))
  (pin (net-name ?nn1) (pin-y ?py&:(< ?py ?y)) (pin-channel-side left | right))
  (not (pin (net-name ?nn) (pin-y ?qz38&:(and (> ?qz38 ?py) (< ?qz38 ?y))) (pin-channel-side left | right)))
  =>
  (retract ?t)
)

(defrule p409
  (context (present choose-between-nets-on-the-same-row-col-con))
  (total (net-name ?nn) (row-col row) (coor ?y) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil))
  ?t <- (total (net-name ?nn1&~?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non&~nil))
  (pin (net-name ?nn1) (pin-y ?py&:(> ?py ?y)) (pin-channel-side left | right))
  (not (pin (net-name ?nn) (pin-y ?qz38&:(and (> ?qz38 ?y) (< ?qz38 ?py))) (pin-channel-side left | right)))
  =>
  (retract ?t)
)

(defrule p410
  (context (present choose-between-nets-on-the-same-row-col-con))
  (total (net-name ?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?non))
  (not (total (cong nil)))
  ?t <- (total (net-name ~?nn) (level-pins ?cou) (total-pins ?cou2) (cong ?qw74&:(<= ?qw74 ?non)))
  =>
  (retract ?t)
)

(defrule p411
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?nn1) (row-col ?rc1) (coor ?xy1) (level-pins ?cou) (total-pins ?qw86&:(> ?qw86 ?cou)))
  ?t1 <-  (total (net-name ?nn2) (row-col ?rc2&~?rc1) (coor ?xy2) (level-pins ?cou) (total-pins ?qw87&:(> ?qw87 ?cou)))
  ?t2 <- (total (net-name ~?nn2) (row-col ?rc2) (coor ?xy2) (level-pins ?cou) (total-pins ?qw88&:(> ?qw88 ?cou)))
  =>
  (retract ?t1 ?t2)
)

(defrule p412
  (context (present choose-between-nets-on-the-same-row-col))
  (total (net-name ?nn1) (row-col ?rc1) (coor ?xy1) (level-pins ?cou) (total-pins ?qw86&:(> ?qw86 ?cou)))
  ?t1 <-  (total (net-name ?nn2) (row-col ?rc2) (coor ?xy2&~?xy1) (level-pins ?cou) (total-pins ?qw87&:(> ?qw87 ?cou)))
  ?t2 <- (total (net-name ~?nn2) (row-col ?rc2) (coor ?xy2) (level-pins ?cou) (total-pins ?qw88&:(> ?qw88 ?cou)))
  =>
  (retract ?t1 ?t2)
)

(defrule p413
  (context (present find-no-of-vcg-hcg))
  (net (net-name ?nn) (no-of-bottom-pins ?np&:(> ?np 1)) (net-is-routed ~yes))
  (not (total-verti ?nn bottom ? ?))
  (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?garb2) (grid-layer ?garb3) (pin-name ?pn))
  (pin (net-name ?nn) (pin-name ?pn) (pin-channel-side bottom))
  =>
  (assert (total-verti ?nn bottom 0 ?np))
)
