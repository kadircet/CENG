`timescale 1ns / 1ps

module VBEncoder(input CLK,
			input [7:0] INT4,input [7:0] INT3, input [7:0] INT2, input [7:0] INT1,
            input START,
            output reg READY, 
			output reg [7:0] STREAM);
//Write your code below

reg [31:0] number;
reg state;
initial
begin
	number=0;
	state=0;
	READY=1;
end

always @(posedge CLK)
begin
	if(START==1 && state==0)
	begin
		state=1;
		READY=0;
		number={INT4,INT3,INT2,INT1};
	end
	
	if(state==1)
	begin
		STREAM={number<128, number[6:0]};
		number=number>>7;
	end
	
	if(number==0)
	begin
		state=0;
		READY=1;
		//RESET STREAM??
	end
end

endmodule