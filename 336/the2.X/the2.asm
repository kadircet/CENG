;Kadir Cetinkaya - 2036457
;Ahmet Alperen Akcay - 2035566
    
LIST	P=18F8722
#include<p18f8722.inc>

CONFIG OSC = HSPLL, FCMEN = OFF, IESO = OFF, PWRT = OFF, BOREN = OFF, WDT = OFF, MCLRE = ON, LPT1OSC = OFF, LVP = OFF, XINST = OFF, DEBUG = OFF

NF udata 0x20
NF
lives udata 0x21
lives
L udata 0x22
L
toggled udata 0x23
toggled
pressed udata 0x24
pressed
NF1 udata 0x25
NF1
NF2 udata 0x26
NF2
L1 udata 0x27
L1
L2 udata 0x28
L2
posx udata 0x29
posx
posy udata 0x2a
posy
time udata 0x2b
time
time1 udata 0x2c
time1
time2 udata 0x2d
time2
toggledb udata 0x2e
toggledb
w_temp udata 0x2f
w_temp
status_temp udata 0x30
status_temp
pclath_temp udata 0x31
pclath_temp
pressedb udata 0x32
pressedb
dir udata 0x33 ; 0 for south 1 for north 2 for east 3 for west
dir
tmrcnt udata 0x34
tmrcnt
intcnt udata 0x35
intcnt
ointcnt udata 0x36
ointcnt
_X udata 0x37
_X
_Y udata 0x38
_Y
fruit1 udata 0x39
fruit1
fruit2 udata 0x3a
fruit2
fruit3 udata 0x3b
fruit3
fruit0 udata 0x3c
fruit0
NGENERATED udata 0x3d
NGENERATED
firstfruit udata 0x3e
firstfruit
temp udata 0x3f
temp
NG2 udata 0x40
NG2
SC1 udata 0x41
SC1
SC2 udata 0x42
SC2

org 0x00
    goto phase1
    
org 0x08
    goto timercheck

org 0x18
    goto rbcheck
    
table:
    rlncf   WREG, W    ; multiply index X2
    addwf   PCL, f	    ; modify program counter
    retlw   B'00111111'  ; 7-Segment = 0
    retlw   B'00000110'  ; 7-Segment = 1
    retlw   B'01011011'  ; 7-Segment = 2
    retlw   B'01001111'  ; 7-Segment = 3
    retlw   B'01100110'  ; 7-Segment = 4
    retlw   B'01101101'  ; 7-Segment = 5
    retlw   B'01111101'  ; 7-Segment = 6
    retlw   B'00000111'  ; 7-Segment = 7
    retlw   B'01111111'  ; 7-Segment = 8
    retlw   B'01100111'  ; 7-Segment = 9

table2:
    rlncf   WREG, W ; multiply index X2
    addwf   PCL, f  ; modify program counter
    retlw   0x0
    retlw   0x1
    retlw   0x3
    retlw   0x7
    retlw   0xf
    retlw   0x1f
    retlw   0x3f

table3:
    rlncf   WREG, W ; multiply index X2
    addwf   PCL, f  ; modify program counter
    retlw   0x1
    retlw   0x2
    retlw   0x4
    retlw   0x8

getNextPos:
    movlw   b'00001111'
    xorwf   temp, W
    BZ	    p33
    movlw   b'00001101'
    xorwf   temp, W
    BZ	    p31
    movlw   b'00001011'
    xorwf   temp, W
    BZ	    p23
    movlw   b'00000101'
    xorwf   temp, W
    BZ	    p11
    movlw   b'00001010'
    xorwf   temp, W
    BZ	    p22
    retlw   b'00001111'
p23:
    retlw   b'00000101'
p31:
    retlw   b'00001011'
p11:
    retlw   b'00001010'
p22:
    retlw   b'00000110'
p33:
    retlw   b'00001101'
    
rbcheck:
    call    save_registers
    btfss   INTCON, 0	;check if rb interrupt
    goto    finish
    
    btfsc   pressedb, 4
    goto    bpr4
    btfss   PORTB, 4	;check if rb4 pressed
    goto    bend4
    bsf	    pressedb, 4
bpr4:
    btfsc   PORTB, 4	;check if rb4 released
    goto    bend4
    bcf	    pressedb, 4
    ;bsf	    toggledb, 4
    clrf    dir
    bsf	    dir, 2
bend4:
    btfsc   pressedb, 5
    goto    bpr5
    btfss   PORTB, 5	;check if rb5 pressed
    goto    bend5
    bsf	    pressedb, 5
bpr5:
    btfsc   PORTB, 5	;check if rb5 released
    goto    bend5
    bcf	    pressedb, 5
    ;bsf	    toggledb, 5
    clrf    dir
    bsf	    dir, 3
bend5:
    btfsc   pressedb, 6
    goto    bpr6
    btfss   PORTB, 6	;check if rb6 pressed
    goto    bend6
    bsf	    pressedb, 6
bpr6:
    btfsc   PORTB, 6	;check if rb6 released
    goto    bend6
    bcf	    pressedb, 6
    ;bsf	    toggledb, 6
    clrf    dir
    bsf	    dir, 1
bend6:
    btfsc   pressedb, 7
    goto    bpr7
    btfss   PORTB, 7	;check if rb7 pressed
    goto    bend7
    bsf	    pressedb, 7
bpr7:
    btfsc   PORTB, 7	;check if rb7 released
    goto    bend7
    bcf	    pressedb, 7
    ;bsf	    toggledb, 7
    clrf    dir
    bsf	    dir, 0
bend7:
    bcf	    INTCON, 0
    goto    finish
    
timercheck:
    call    save_registers
    btfss   INTCON, 2
    goto    finish
    
    movlw   d'145'
    movwf   TMR0
    bcf	    INTCON, 2
    
    decfsz  tmrcnt
    goto    finish
    
    movlw   d'11'
    movwf   tmrcnt
    movff   intcnt, ointcnt
    incf    intcnt
    call    updatetimer
finish:
    call    restore_registers
    retfie
    
save_registers:
    movwf  w_temp          ;Copy W to TEMP register
    swapf  STATUS, w       ;Swap status to be saved into W
    clrf  STATUS          ;bank 0, regardless of current bank, Clears IRP,RP1,RP0
    movwf  status_temp     ;Save status to bank zero STATUS_TEMP register
    movf  PCLATH, w       ;Only required if using pages 1, 2 and/or 3
    movwf  pclath_temp     ;Save PCLATH into W
    clrf  PCLATH          ;Page zero, regardless of current page
    return

restore_registers:
    movf  pclath_temp, w  ;Restore PCLATH
    movwf  PCLATH          ;Move W into PCLATH
    swapf  status_temp, w  ;Swap STATUS_TEMP register into W
    movwf  STATUS          ;Move W into STATUS register
    swapf  w_temp, f       ;Swap W_TEMP
    swapf  w_temp, w       ;Swap W_TEMP into W
    return
    
updatetimer:
    movlw   0x1f
    andwf   intcnt, W
    BNZ	    finishtmr
    
    movlw   0x09
    xorwf   time2, W
    BZ	    awctime
    incf    time2
    return
awctime:
    movlw   0x09
    xorwf   time1, W
    BZ	    setzero
    incf    time1
    clrf    time2
    return
setzero:
    clrf    time1
    clrf    time2
finishtmr:
    return
    
toggleLed:
    movf    _Y, W
    call    table3
    movwf   _Y
    
    movlw   0x0
    xorwf   _X, W
    BZ	    toggleC
    movlw   0x1
    xorwf   _X, W
    BZ	    toggleD
    movlw   0x2
    xorwf   _X, W
    BZ	    toggleE
toggleF:
    movf    _Y, W
    xorwf   LATF
    return
toggleE:
    movf    _Y, W
    xorwf   LATE
    return
toggleD:
    movf    _Y, W
    xorwf   LATD
    return
toggleC:
    movf    _Y, W
    xorwf   LATC
    return    
    
turnonLed:
    movf    _Y, W
    call    table3
    movwf   _Y
    
    movlw   0x0
    xorwf   _X, W
    BZ	    turnonC
    movlw   0x1
    xorwf   _X, W
    BZ	    turnonD
    movlw   0x2
    xorwf   _X, W
    BZ	    turnonE
turnonF:
    movf    _Y, W
    iorwf   LATF
    return
turnonE:
    movf    _Y, W
    iorwf   LATE
    return
turnonD:
    movf    _Y, W
    iorwf   LATD
    return
turnonC:
    movf    _Y, W
    iorwf   LATC
    return
    
turnoffLed:
    movf    _Y, W
    call    table3
    movwf   _Y
    
    movlw   0x0
    xorwf   _X, W
    BZ	    turnoffC
    movlw   0x1
    xorwf   _X, W
    BZ	    turnoffD
    movlw   0x2
    xorwf   _X, W
    BZ	    turnoffE
turnoffF:
    movf    _Y, W
    comf    WREG
    andwf   LATF
    return
turnoffE:
    movf    _Y, W
    comf    WREG
    andwf   LATE
    return
turnoffD:
    movf    _Y, W
    comf    WREG
    andwf   LATD
    return
turnoffC:
    movf    _Y, W
    comf    WREG
    andwf   LATC
    return
    
checkToggles:
    btfsc   pressed, 0
    goto    pr0
    btfss   PORTC, 0	;check if rc0 pressed
    goto    end0
    bsf	    pressed, 0
pr0:
    btfsc   PORTC, 0	;check if rc0 released
    goto    end0
    bcf	    pressed, 0
    bsf	    toggled, 0
end0:
    btfsc   pressed, 1
    goto    pr1
    btfss   PORTC, 1	;check if rc1 pressed
    goto    end1
    bsf	    pressed, 1
pr1:
    btfsc   PORTC, 1	;check if rc1 released
    goto    end1
    bcf	    pressed, 1
    bsf	    toggled, 1
end1:
    btfsc   pressed, 2
    goto    pr2
    btfss   PORTC, 2	;check if rc2 pressed
    goto    end2
    bsf	    pressed, 2
pr2:
    btfsc   PORTC, 2	;check if rc2 released
    goto    end2
    bcf	    pressed, 2
    bsf	    toggled, 2
end2:
    btfsc   pressed, 3
    goto    pr3
    btfss   PORTC, 3	;check if rc3 pressed
    goto    end3
    bsf	    pressed, 3
pr3:
    btfsc   PORTC, 3	;check if rc3 released
    goto    end3
    bcf	    pressed, 3
    bsf	    toggled, 3
end3:
    btfsc   pressed, 4
    goto    pr4
    btfss   PORTC, 4	;check if rc4 pressed
    goto    end4
    bsf	    pressed, 4
pr4:
    btfsc   PORTC, 4	;check if rc4 released
    goto    end4
    bcf	    pressed, 4
    bsf	    toggled, 4
end4:
    btfsc   pressed, 5
    goto    pr5
    btfss   PORTC, 5	;check if rc5 pressed
    goto    end5
    bsf	    pressed, 5
pr5:
    btfsc   PORTC, 5	;check if rc5 released
    goto    end5
    bcf	    pressed, 5
    bsf	    toggled, 5
end5:
    btfsc   pressed, 6
    goto    pr6
    btfss   PORTA, 4	;check if ra4 pressed
    goto    end6
    bsf	    pressed, 6
pr6:
    btfsc   PORTA, 4	;check if ra4 released
    goto    end6
    bcf	    pressed, 6
    bsf	    toggled, 6
end6:
    return
    
xdecNF:
    decf    NF		;decrement N by 1
    tstfsz  NF2
    goto    swocx
    decf    NF1
    movlw   0x09
    movwf   NF2
    goto    xend0x
swocx:
    decf    NF2
xend0x:
    return
    
incScore:
    movlw   0x09
    xorwf   SC2, W
    BZ	    awcS
    incf    SC2
    goto    incSend
awcS:
    incf    SC1
    clrf    SC2
incSend:
    call    decNF
    return

processToggles:
    btfss   toggled, 0	;check if rc0 toggled
    goto    xend0
    bcf	    toggled, 0	;clear rc0 toggle
    tstfsz  NF		;check if N is 0
    goto    decNF
    goto    xend0
decNF:
    decf    NF		;decrement N by 1
    tstfsz  NF2
    goto    swoc
    decf    NF1
    movlw   0x09
    movwf   NF2
    goto    xend0
swoc:
    decf    NF2
xend0:
    btfss   toggled, 1	;check if rc1 toggled
    goto    xend1
    bcf	    toggled, 1	;clear rc1 toggle
    movlw   d'99'
    xorwf   NF, W
    BZ	    xend1
    incf    NF		;increment N by 1
    movlw   0x09
    xorwf   NF2, W
    BZ	    awc
    incf    NF2
    goto    xend1
awc:
    incf    NF1
    clrf    NF2
xend1:
    btfss   toggled, 2	;check if rc2 toggled
    goto    xend2
    bcf	    toggled, 2	;clear rc2 toggle
    tstfsz  lives	;check if lives is 0
    decf    lives
    goto    xend2
xend2:
    btfss   toggled, 3	;check if rc3 toggled
    goto    xend3
    bcf	    toggled, 3	;clear rc3 toggle
    movlw   0x06
    xorwf   lives, W
    BZ	    xend3
    incf    lives	;increment lives by 1
xend3:
    btfss   toggled, 4	;check if rc4 toggled
    goto    xend4
    bcf	    toggled, 4	;clear rc4 toggle
    tstfsz  L		;check if lives is 0
    decf    L		;decrement lives by 1
    goto    xend4
xend4:
    btfss   toggled, 5	;check if rc5 toggled
    goto    xend5
    bcf	    toggled, 5	;clear rc5 toggle
    movlw   0x03
    xorwf   L, W
    BZ	    xend5
    incf    L		;increment L by 1
xend5:
    return
    
updateDisplay1:
    clrf    LATH
    movf    L, 0
    call    table
    movwf   LATJ
    bsf	    LATH, 0
    call    DELAY
    
    movf    lives, 0
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY

    movf    NF1, 0
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY
    
    movf    NF2, 0
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY
    return
    
checkCollide:
    movf    _X, W
    xorwf   posx, W
    tstfsz  WREG
    retlw   0x00
    movf    _Y, W
    xorwf   posy, W
    tstfsz  WREG
    retlw   0x00
    retlw   0x10
    
updateDisplay2:
    clrf    LATH
    movf    NF1, W
    call    table
    movwf   LATJ
    bsf	    LATH, 0
    call    DELAY
    
    movf    NF2, W
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY

    movf    time1, W
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY
    
    movf    time2, W
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY
    
    movf    ointcnt, W
    xorwf   intcnt, W
    tstfsz  WREG
    goto    con1
    goto    finishd2
con1:
    
    movf    L, W
    call    table2
    andwf   intcnt, W
    tstfsz  WREG
    goto    finishd2
    
    movff   posx, _X
    movff   posy, _Y
    call    toggleLed
    
    movf    L, W
    call    table2
    rlncf   WREG
    incf    WREG
    rlncf   WREG
    incf    WREG
    andwf   intcnt, W
    tstfsz  WREG
    goto    finishd2
    
    movff   posx, _X
    movff   posy, _Y
    call    turnonLed
    movff   posx, _X
    movff   posy, _Y
    call    toggleLed	;turnoff current snake position
    
    btfsc   dir, 0
    goto    MOVES
    btfsc   dir, 1
    goto    MOVEN
    btfsc   dir, 2
    goto    MOVEE
MOVEW:
    movlw   0x00
    xorwf   posx, W
    BZ	    COLLIDE
    decf    posx
    goto    finmove
MOVEE:
    movlw   0x03
    xorwf   posx, W
    BZ	    COLLIDE
    incf    posx
    goto    finmove
MOVEN:
    movlw   0x00
    xorwf   posy, W
    BZ	    COLLIDE
    decf    posy
    goto    finmove
MOVES:
    movlw   0x03
    xorwf   posy, W
    BZ	    COLLIDE
    incf    posy
    goto    finmove
COLLIDE:
    decf    lives
    movf    lives, W
    call    table2
    movwf   LATA
finmove:

cfruit0:
    btfss   fruit0, 4
    goto    cfruit1
    movlw   b'00001100'
    andwf   fruit0, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit0, W
    movwf   _Y
    call    checkCollide
    xorwf   fruit0
    tstfsz  WREG
    call    incScore
cfruit1:
    btfss   fruit1, 4
    goto    cfruit2
    movlw   b'00001100'
    andwf   fruit1, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit1, W
    movwf   _Y
    call    checkCollide
    xorwf   fruit1
    tstfsz  WREG
    call    incScore
cfruit2:
    btfss   fruit2, 4
    goto    cfruit3
    movlw   b'00001100'
    andwf   fruit2, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit2, W
    movwf   _Y
    call    checkCollide
    xorwf   fruit2
    tstfsz  WREG
    call    incScore
cfruit3:
    btfss   fruit3, 4
    goto    endeat
    movlw   b'00001100'
    andwf   fruit3, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit3, W
    movwf   _Y
    call    checkCollide
    xorwf   fruit3
    tstfsz  WREG
    call    incScore
endeat:
    
    movf    L, W
    call    table2
    rlncf   WREG
    incf    WREG
    rlncf   WREG
    incf    WREG
    rlncf   WREG
    incf    WREG
    andwf   intcnt, W
    tstfsz  WREG
    goto    finishd2
    
;fruit format -> 0|0|0|ENABLED|X1|X0|Y1|Y0|
;		 7|6|5|    4  |3 | 2| 1| 0|
    
    movlw   b'00001100'
    andwf   fruit0, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit0, W
    movwf   _Y
    call    turnoffLed

    movlw   b'00001100'
    andwf   fruit1, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit1, W
    movwf   _Y
    call    turnoffLed
    
    movlw   b'00001100'
    andwf   fruit2, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit2, W
    movwf   _Y
    call    turnoffLed
    
    movlw   b'00001100'
    andwf   fruit3, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit3, W
    movwf   _Y
    call    turnoffLed
    
    movlw   0x00
    xorwf   NGENERATED, W
    BZ	    zerothf
    movlw   0x01
    xorwf   NGENERATED, W
    BZ	    firstf
    movlw   0x02
    xorwf   NGENERATED, W
    BZ	    secondf
thirdf:
    btfsc   fruit3, 4
    call    xdecNF
    movlw   b'00001111'
    andwf   fruit2, W
    movwf   temp
    call    getNextPos
    movwf   fruit3
    tstfsz  NG2
    bsf	    fruit3, 4
    goto    finishfruit
secondf:
    btfsc   fruit2, 4
    call    xdecNF
    movlw   b'00001111'
    andwf   fruit1, W
    movwf   temp
    call    getNextPos
    movwf   fruit2
    tstfsz  NG2
    bsf	    fruit2, 4
    goto    finishfruit
firstf:
    btfsc   fruit1, 4
    call    xdecNF
    movlw   b'00001111'
    andwf   fruit0, W
    movwf   temp
    call    getNextPos
    movwf   fruit1
    tstfsz  NG2
    bsf	    fruit1, 4
    goto    finishfruit
zerothf:
    tstfsz  firstfruit
    goto    firstgen
    btfsc   fruit0, 4
    call    xdecNF
    movlw   b'00001111'
    andwf   fruit3, W
    movwf   temp
    call    getNextPos
    movwf   fruit0
    tstfsz  NG2
    bsf	    fruit0, 4
    goto    finishfruit
firstgen:
    movlw   b'00011111'
    movwf   fruit0
finishfruit:
    tstfsz  NG2
    decf    NG2
    incf    NGENERATED
    movlw   0x03
    andwf   NGENERATED
    clrf    firstfruit
    
    movlw   b'00001100'
    andwf   fruit0, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit0, W
    movwf   _Y
    btfsc   fruit0, 4
    call    turnonLed

    movlw   b'00001100'
    andwf   fruit1, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit1, W
    movwf   _Y
    btfsc   fruit1, 4
    call    turnonLed
    
    movlw   b'00001100'
    andwf   fruit2, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit2, W
    movwf   _Y
    btfsc   fruit2, 4
    call    turnonLed
    
    movlw   b'00001100'
    andwf   fruit3, W
    rrncf   WREG
    rrncf   WREG
    movwf   _X
    movlw   b'00000011'
    andwf   fruit3, W
    movwf   _Y
    btfsc   fruit3, 4
    call    turnonLed    
finishd2:
    movff   intcnt, ointcnt
    return
    
updateDisplay3:
    clrf    LATH
    tstfsz  lives
    goto    satisfactory

    movlw   b'00111110'
    movwf   LATJ
    bsf	    LATH, 0
    call    DELAY
    goto    rest
    
satisfactory:
    movlw   b'01101101'
    movwf   LATJ
    bsf	    LATH, 0
    call    DELAY
    
    
rest:
    movf    lives, W
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY

    movf    SC1, W
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY
    
    movf    SC2, W
    call    table
    movwf   LATJ
    rlncf   LATH
    call    DELAY
    
    return
    
phase1:
    movlw   b'00010000'	;ra4 input rest output
    movwf   TRISA
    movlw   b'00111111' ;rc0-5 input
    movwf   TRISC
    movlw   b'11110000' ;rh0-3 output
    clrf    TRISH
    clrf    TRISJ	;rj is output
    clrf    TRISD
    clrf    TRISB
    clrf    TRISE
    clrf    TRISE
    clrf    TRISF
    clrf    TRISG
    clrf    LATA
    clrf    LATB
    clrf    LATC
    clrf    LATD
    clrf    LATE
    clrf    LATF
    clrf    LATG
    clrf    LATH
    clrf    LATJ
    clrf    INTCON
    clrf    INTCON2
    setf    ADCON1	;ra is all digital
    
    movlw   0x0f
    movwf   NF
    movlw   0x01
    movwf   NF1
    movlw   0x05
    movwf   NF2
    movlw   0x03
    movwf   lives
    movlw   0x03
    movwf   L
    clrf    toggled
    clrf    pressed
    clrf    NGENERATED
    clrf    fruit0
    clrf    fruit1
    clrf    fruit2
    clrf    SC1
    clrf    SC2
    clrf    fruit3
phase1L:
    call    checkToggles
    call    processToggles
    call    updateDisplay1
    
    btfss   toggled, 6	;check if ra4 toggled
    goto    phase1L
    bcf	    toggled, 6
    
phase2:
    clrf    TRISA
    clrf    TRISC
    clrf    TRISD
    clrf    TRISE
    clrf    TRISF
    clrf    LATA
    clrf    LATC
    clrf    LATD
    clrf    LATE
    clrf    LATF
    clrf    intcnt
    clrf    tmrcnt
    clrf    ointcnt
    clrf    posx
    clrf    posy
    clrf    time
    clrf    time1
    clrf    time2
    clrf    NG2
    movff   NF, NG2
    clrf    dir
    setf    firstfruit
    bsf	    dir, 0
    movlw   d'11'
    movwf   tmrcnt
    movf    lives, W
    call    table2
    movwf   LATA
    movlw   b'11110000'
    movwf   TRISB
    bsf	    RCON, 7
    bsf	    INTCON2, 7
    bsf	    INTCON2, 2
    bcf	    INTCON2, 0
    clrf    PORTB
    movlw   b'11101000'	;enable global,timer,rb
    movwf   INTCON
    movlw   d'145'
    movwf   TMR0
    movlw   b'01000111'
    movwf   T0CON
    bsf	    T0CON, 7
    
phase2L:
    call    updateDisplay2
    tstfsz  lives
    goto    nfChk
    goto    phase3
nfChk:
    tstfsz  NF
    goto    phase2L
    
phase3:
    clrf    INTCON
    clrf    INTCON2
    movlw   b'00010000'	;ra4 input rest output
    movwf   TRISA
    clrf    TRISB
    clrf    LATA
    clrf    LATC
    clrf    LATD
    clrf    LATE
    clrf    LATF
    
phase3L:
    call    checkToggles
    call    updateDisplay3
    
    btfss   toggled, 6
    goto    phase3L
    goto    phase1
    
DELAY:                          ; Time Delay Routines
    movlw 50                        ; Copy 50 to W
    movwf L2                    ; Copy W into L2

LOOP2:
    movlw 255                   ; Copy 255 into W
    movwf L1                    ; Copy W into L1

LOOP1:
    decfsz L1,F                    ; Decrement L1. If 0 Skip next instruction
        goto LOOP1                ; ELSE Keep counting down
    decfsz L2,F                    ; Decrement L2. If 0 Skip next instruction
        goto LOOP2                ; ELSE Keep counting down
    return
    
end