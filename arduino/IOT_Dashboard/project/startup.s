        ;===============================
        ; LPC1768 Startup File (ASM)
        ;===============================

        ; ----- Vector Table -----
        AREA    RESET, DATA, READONLY
        EXPORT  __Vectors

__Vectors
        DCD     0x10008000          ; Initial MSP (end of 32 KB RAM)
        DCD     Reset_Handler       ; Reset
        DCD     0                   ; NMI
        DCD     0                   ; HardFault
        DCD     0                   ; MemManage
        DCD     0                   ; BusFault
        DCD     0                   ; UsageFault
        DCD     0,0,0,0             ; Reserved
        DCD     0                   ; SVCall
        DCD     0                   ; Debug Monitor
        DCD     0                   ; Reserved
        DCD     0                   ; PendSV
        DCD     0                   ; SysTick
        ; (More IRQ vectors exist but not required for startup)

        ; ----- Code Area -----
        AREA    |.text|, CODE, READONLY
        THUMB
        REQUIRE __Vectors
        EXPORT  Reset_Handler
        EXPORT  __main

; ---------------- RESET HANDLER ----------------
Reset_Handler
        BL      __main        ; Call main
stop_here
        B       stop_here     ; Hang forever

; ---------------- USER MAIN FUNCTION ----------------
__main
        MOV     R0, #123      ; Your original code
        NOP
        NOP
        BX      LR            ; return to Reset_Handler (optional)

        END
