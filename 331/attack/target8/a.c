void test()
{
	__asm__("push %rax");
	__asm__("push $0x123");
	__asm__("jmp 0x123");
}
