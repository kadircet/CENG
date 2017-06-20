LIST	P=18F8722
#include<p18f8722.inc>

CONFIG OSC = HSPLL, FCMEN = OFF, IESO = OFF, PWRT = OFF, BOREN = OFF, WDT = OFF, MCLRE = ON, LPT1OSC = OFF, LVP = OFF, XINST = OFF, DEBUG = OFF

toggle udata 0x20
toggle
 
rc4p udata 0x24
rc4p
 
i udata 0x21
i

j udata 0x22
j

k udata 0x23
k
 
org 0; start code at 0
    goto setup
 
org 0x8
    goto $

setup:
    movlw   b'00010000'	;rx4 input rest output
    movwf   TRISA
    movwf   TRISC
    clrf    TRISB
    clrf    TRISD
    clrf    toggle
    clrf    rc4p
    setf    ADCON1
    
    movlw   b'00001111'
    movwf   LATA
    movwf   LATD
    
    movlw   b'00001001'
    movwf   LATB
    movwf   LATC
    
press:
    btfss   PORTA, 4	;check if ra4 pressed
    goto    press	;skip if pressed
release:
    btfsc   PORTA, 4	;check if ra4 released
    goto    release	;skip if released
    
state1:
    clrf    LATA
    clrf    LATB
    clrf    LATC
    clrf    toggle
    movlw   b'00000001'
    movwf   LATD    ;rd0
    call    wait
    tstfsz  toggle
    goto    state2
    rlncf   LATD    ;rd1
    call    wait
    tstfsz  toggle
    goto    state2
    rlncf   LATD    ;rd2
    call    wait
    tstfsz  toggle
    goto    state2
    rlncf   LATD    ;rd3
    call    wait
    tstfsz  toggle
    goto    state2
    clrf    LATD
    movlw   b'00001000'
    movwf   LATC    ;rc3
    call    wait
    tstfsz  toggle
    goto    state2
    clrf    LATC
    movlw   b'00001000'
    movwf   LATB    ;rb3
    call    wait
    tstfsz  toggle
    goto    state2
    clrf    LATB
    movlw   b'00001000'
    movwf   LATA    ;ra3
    call    wait
    tstfsz  toggle
    goto    state2
    rrncf   LATA    ;ra2
    call    wait
    tstfsz  toggle
    goto    state2
    rrncf   LATA    ;ra1
    call    wait
    tstfsz  toggle
    goto    state2
    rrncf   LATA    ;ra0
    call    wait
    tstfsz  toggle
    goto    state2
    clrf    LATA
    movlw   b'00000001'
    movwf   LATB    ;rb0
    call    wait
    tstfsz  toggle
    goto    state2
    clrf    LATB
    movlw   b'00000001'
    movwf   LATC    ;rc0
    call    wait
    tstfsz  toggle
    goto    state2
    clrf    LATC
    call    waittoggle
    goto    state2

state2:
    clrf    LATA
    clrf    LATB
    clrf    LATC
    clrf    toggle
    movlw   b'00000001'
    movwf   LATD    ;rd0
    call    wait
    tstfsz  toggle
    goto    state1
    rlncf   LATD    ;rd1
    call    wait
    tstfsz  toggle
    goto    state1
    rlncf   LATD    ;rd2
    call    wait
    tstfsz  toggle
    goto    state1
    clrf    LATD
    movlw   b'00000100'
    movwf   LATC    ;rc2
    call    wait
    tstfsz  toggle
    goto    state1
    clrf    LATC
    movlw   b'00000100'
    movwf   LATB    ;rb2
    call    wait
    tstfsz  toggle
    goto    state1
    rrncf   LATB    ;rb1
    call    wait
    tstfsz  toggle
    goto    state1
    rrncf   LATB    ;rb0
    call    wait
    tstfsz  toggle
    goto    state1
    clrf    LATB
    movlw   b'00000001'
    movwf   LATC    ;rc0
    call    wait
    tstfsz  toggle
    goto    state1
    clrf    LATC
    call    waittoggle
    goto    state1

waittoggle:
press2:
    btfss   PORTC, 4	;check if rc4 pressed
    goto    press2	;skip if pressed
release2:
    btfsc   PORTC, 4	;check if rc4 released
    goto    release2	;skip if released
    return

    
wait:
    movlw   0xff
    movwf   i
l1:
    movlw   0xff
    movwf   j
l2:
    movlw   0x14
    movwf   k
l3:
    tstfsz  rc4p
    goto    pressed
    btfss   PORTC, 4	;check if rc4 pressed
    goto    endifx
    setf    rc4p
pressed:
    btfsc   PORTC, 4	;check if rc4 released
    goto    endifx
    clrf    rc4p
    setf    toggle
    return
endifx:
    decfsz  k
    goto    l3
    decfsz  j
    goto    l2
    nop
    decfsz  i
    goto    l1
    return

end