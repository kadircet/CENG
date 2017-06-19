`timescale 1ns / 1ps

module testbench(
    );
	reg CLK;
	//reg [7:0] INT4;
	//reg[7:0] INT3;
	//reg [7:0] INT2;
	reg [7:0] INT1;
	reg START;
	wire READY;
	wire[7:0] STREAM;
 	 parameter INT4 = 8'b00000000;
	 parameter INT3 = 8'b01100110;
	 parameter INT2=  8'b01100110;
integer sayi={INT3,INT3 ,INT3 ,8'b11111111 };

	
 LAB5_2 ins (CLK,
					INT4,INT3,INT2,INT1,
               START,
               READY, 
					STREAM);
	
	
 initial CLK =0;
	always #5 CLK = ~CLK;
   
	initial begin
        // set monitor to inputs and outputs
	#1;


	INT1=8'b01100110;
	START=1;
	#10;
	START=0;
	#100;
	
	
	$finish;
        
	end
endmodule
