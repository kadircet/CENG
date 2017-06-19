	.file	"ncopy.c"
	.text
	.globl	ncopy
	.type	ncopy, @function
ncopy:
.LFB11:
	.cfi_startproc
	testb	%dl, %dl
	je	.L5
	subl	$1, %edx
	movzbl	%dl, %edx
	addq	$1, %rdx
	movl	$0, %ecx
	movl	$0, %eax
.L4:
	movzbl	(%rdi,%rcx), %r8d
	movb	%r8b, (%rsi,%rcx)
	cmpb	$1, %r8b
	sbbb	$-1, %al
	addq	$1, %rcx
	cmpq	%rdx, %rcx
	jne	.L4
	rep ret
.L5:
	movl	%edx, %eax
	ret
	.cfi_endproc
.LFE11:
	.size	ncopy, .-ncopy
	.section	.rodata.str1.1,"aMS",@progbits,1
.LC0:
	.string	"count=%d\n"
	.text
	.globl	main
	.type	main, @function
main:
.LFB12:
	.cfi_startproc
	subq	$8, %rsp
	.cfi_def_cfa_offset 16
	movl	$src, %edx
	movl	$0, %eax
.L8:
	addl	$1, %eax
	movb	%al, (%rdx)
	addq	$1, %rdx
	cmpb	$8, %al
	jne	.L8
	movl	$8, %edx
	movl	$dst, %esi
	movl	$src, %edi
	call	ncopy
	movzbl	%al, %esi
	movl	$.LC0, %edi
	movl	$0, %eax
	call	printf
	movl	$0, %edi
	call	exit
	.cfi_endproc
.LFE12:
	.size	main, .-main
	.comm	dst,8,8
	.comm	src,8,8
	.ident	"GCC: (GNU) 6.2.1 20160830"
	.section	.note.GNU-stack,"",@progbits
