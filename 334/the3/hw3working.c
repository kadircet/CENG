#include <fcntl.h>
#include <sys/mman.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include "ext2.h"

#define BASE_OFFSET 1024
#define BLOCK_SIZE 1024

typedef unsigned char bmap;
#define __NBITS (8 * (int) sizeof (bmap))
#define __BMELT(d) ((d) / __NBITS)
#define __BMMASK(d) ((bmap) 1 << ((d) % __NBITS))
#define BM_SET(d, set) ((set[__BMELT (d)] |= __BMMASK (d)))
#define BM_CLR(d, set) ((set[__BMELT (d)] &= ~__BMMASK (d)))
#define BM_ISSET(d, set) ((set[__BMELT (d)] & __BMMASK (d)) != 0)
#define BLOCK_OFFSET(block) (BASE_OFFSET + (block-1)*BLOCK_SIZE)

int readSuperBlock(int fd, struct ext2_super_block* super)
{
    lseek(fd, BASE_OFFSET, SEEK_SET);
    read(fd, super, sizeof(*super));

    return super->s_magic != EXT2_SUPER_MAGIC;
}

int readGroupBlock(int fd, struct ext2_super_block super, int bgid, struct ext2_group_desc *group)
{
    lseek(fd, BASE_OFFSET+sizeof(super)+sizeof(group)*bgid, SEEK_SET);
    read(fd, group, sizeof(*group));

    return 0;
}

unsigned int *getInodes(int fd, struct ext2_super_block super, struct ext2_group_desc group, int *cnt)
{
    unsigned int *res, i;
	*cnt = 0;
	bmap *bitmap = (bmap*)malloc(BLOCK_SIZE);
    res=malloc(BLOCK_SIZE*8*sizeof(unsigned int));

    lseek(fd, group.bg_inode_bitmap*BLOCK_SIZE, SEEK_SET);
    read(fd, bitmap, BLOCK_SIZE);
    for(i=0;i<super.s_inodes_per_group;i++)
        if(BM_ISSET(i, bitmap))
            res[(*cnt)++] = i+1;

	free(bitmap);
	return res;
}

unsigned int *getBlocks(int fd, struct ext2_super_block super, struct ext2_group_desc group, int *cnt)
{
    unsigned int *res, i;
	*cnt = 0;
	bmap *bitmap = (bmap*)malloc(BLOCK_SIZE);
    res=malloc(BLOCK_SIZE*8*sizeof(unsigned int));

    lseek(fd, group.bg_block_bitmap*BLOCK_SIZE, SEEK_SET);
    read(fd, bitmap, BLOCK_SIZE);
    for(i=0;i<super.s_blocks_count;i++)
        if(BM_ISSET(i, bitmap))
            res[(*cnt)++] = i+1;

	free(bitmap);
	return res;
}

void markBlocks(int fd, unsigned int* inodes, unsigned int icnt, unsigned int *btoInode, 
        unsigned int bg_inode_table, unsigned int* blockCount)
{
    int i,j,k,l,m;
    struct ext2_inode inode;
    unsigned int* block = malloc(BLOCK_SIZE), *block2 = malloc(BLOCK_SIZE),
             *block3 = malloc(BLOCK_SIZE);
    
    for(i=0;i<icnt;i++)
    {
        lseek(fd, bg_inode_table*BLOCK_SIZE+(inodes[i]-1)*sizeof(inode), SEEK_SET);
        read(fd, &inode, sizeof(inode));
        k=0;
        for(j=0;j<15;j++)
        {
            if(inode.i_block[j]==0)
                continue;
            if(blockCount[inodes[i]]==inode.i_blocks/2)
                break;
            blockCount[inodes[i]]++;
            btoInode[inode.i_block[j]] = inodes[i];
            if(inodes[i]>11)
                printf("%u %u\n", inodes[i], inode.i_block[j]);

            if(j==12)//Single Indirect
            {
                lseek(fd, BLOCK_SIZE*inode.i_block[j], SEEK_SET);
                read(fd, block, BLOCK_SIZE);
                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block[k]==0)
                        continue;
                    if(blockCount[inodes[i]]==inode.i_blocks/2)
                        break;
                    btoInode[block[k]]=inodes[i];
                    blockCount[inodes[i]]++;
                    if(inodes[i]>11)
                        printf("%u %u\n", inodes[i], block[k]);
                }
            }
            else if(j==13)//Double Indirect
            {
                lseek(fd, BLOCK_SIZE*inode.i_block[j], SEEK_SET);
                read(fd, block, BLOCK_SIZE);
                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block[k]==0)
                        continue;
                    if(blockCount[inodes[i]]==inode.i_blocks/2)
                        break;
                    btoInode[block[k]]=inodes[i];
                    blockCount[inodes[i]]++;
                    lseek(fd, BLOCK_SIZE*block[k], SEEK_SET);
                    read(fd, block2, BLOCK_SIZE);
                    for(l=0;l<BLOCK_SIZE/sizeof(unsigned int);l++)
                    {
                        if(block2[l]==0)
                            continue;
                        if(blockCount[inodes[i]]==inode.i_blocks/2)
                            break;
                        btoInode[block2[l]]=inodes[i];
                        blockCount[inodes[i]]++;
                        if(inodes[i]>11)
                            printf("%u %u\n", inodes[i], block2[k]);
                    }
                }
            }
            else if(j==14)//Triple Indirect
            {
                lseek(fd, BLOCK_SIZE*inode.i_block[j], SEEK_SET);
                read(fd, block, BLOCK_SIZE);
                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block[k]==0)
                        continue;
                    if(blockCount[inodes[i]]==inode.i_blocks/2)
                        break;
                    btoInode[block[k]]=inodes[i];
                    blockCount[inodes[i]]++;
                    lseek(fd, BLOCK_SIZE*block[k], SEEK_SET);
                    read(fd, block2, BLOCK_SIZE);
                    for(l=0;l<BLOCK_SIZE/sizeof(unsigned int);l++)
                    {
                        if(block2[l]==0)
                            continue;
                        if(blockCount[inodes[i]]==inode.i_blocks/2)
                            break;
                        btoInode[block2[l]]=inodes[i];
                        blockCount[inodes[i]]++;
                        lseek(fd, BLOCK_SIZE*block2[l], SEEK_SET);
                        read(fd, block3, BLOCK_SIZE);
                        for(m=0;m<BLOCK_SIZE/sizeof(unsigned int);l++)
                        {
                            if(block3[m]==0)
                                continue;
                            if(blockCount[inodes[i]]==inode.i_blocks/2)
                                break;
                            btoInode[block3[m]]=inodes[i];
                            blockCount[inodes[i]]++;
                            if(inodes[i]>11)
                                printf("%u %u\n", inodes[i], block3[k]);
                        }
                    }
                }
            }
        }
    }
    free(block);
    free(block2);
    free(block3);
}
unsigned int findFirstEmpty(unsigned int* btoInode)
{
    unsigned int firstEmpty=0;
    while(btoInode[++firstEmpty]==0);
    while(btoInode[++firstEmpty]<12);
    firstEmpty=23;
    return firstEmpty;
}

void swapDataBlocks(int fd, unsigned int b1, unsigned int b2)
{
    void *block1, *block2;
    block1 = malloc(BLOCK_SIZE);
    block2 = malloc(BLOCK_SIZE);

    lseek(fd, b1*BLOCK_SIZE, SEEK_SET);
    read(fd, block1, BLOCK_SIZE);
    lseek(fd, b2*BLOCK_SIZE, SEEK_SET);
    read(fd, block2, BLOCK_SIZE);

    lseek(fd, b1*BLOCK_SIZE, SEEK_SET);
    write(fd, block2, BLOCK_SIZE);
    lseek(fd, b2*BLOCK_SIZE, SEEK_SET);
    write(fd, block1, BLOCK_SIZE);

    free(block1);
    free(block2);
}

void changeTo(int fd, unsigned int ino, unsigned int b1, unsigned int b2, struct ext2_inode* inodeTable)
{
    int i,j,k,l;
    if(ino<12)
        return;
    unsigned int* block = malloc(BLOCK_SIZE), *block2 = malloc(BLOCK_SIZE),
             *block3 = malloc(BLOCK_SIZE);
    for(i=0;i<15;i++)
    {
        if(inodeTable[ino].i_block[i]==0)
            continue;
        if(inodeTable[ino].i_block[i]==b1)
        {
            inodeTable[ino].i_block[i]=b2;
            break;
        }
        
        if(i==12)
        {
            lseek(fd, inodeTable[ino].i_block[i]*BLOCK_SIZE, SEEK_SET);
            read(fd, block, BLOCK_SIZE);

            for(j=0;j<BLOCK_SIZE/sizeof(unsigned int);j++)
                if(block[j]==b1)
                {
                    block[j]=b2;
                    i=15;
                    break;
                }

            lseek(fd, inodeTable[ino].i_block[i]*BLOCK_SIZE, SEEK_SET);
            write(fd, block, BLOCK_SIZE);
        }
        else if(i==13)
        {
            lseek(fd, inodeTable[ino].i_block[i], SEEK_SET);
            read(fd, block, BLOCK_SIZE);

            for(j=0;j<BLOCK_SIZE/sizeof(unsigned int);j++)
            {
                if(block[j]==b1)
                {
                    block[j]=b2;
                    i=15;
                    break;
                }

                lseek(fd, block[j]*BLOCK_SIZE, SEEK_SET);
                read(fd, block2, BLOCK_SIZE);

                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                    if(block2[k]==b1)
                    {
                        block2[k]=b2;
                        j=BLOCK_SIZE;
                        i=15;
                        break;
                    }

                lseek(fd, block[j]*BLOCK_SIZE, SEEK_SET);
                write(fd, block2, BLOCK_SIZE);
            }

            lseek(fd, inodeTable[ino].i_block[i]*BLOCK_SIZE, SEEK_SET);
            write(fd, block, BLOCK_SIZE);
        }
        else if(i==14)
        {
            lseek(fd, inodeTable[ino].i_block[i], SEEK_SET);
            read(fd, block, BLOCK_SIZE);

            for(j=0;j<BLOCK_SIZE/sizeof(unsigned int);j++)
            {
                if(block[j]==b1)
                {
                    block[j]=b2;
                    i=15;
                    break;
                }

                lseek(fd, block[j]*BLOCK_SIZE, SEEK_SET);
                read(fd, block2, BLOCK_SIZE);

                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block2[k]==0)
                        continue;
                    if(block2[k]==b1)
                    {
                        block2[k]=b2;
                        j=BLOCK_SIZE;
                        i=15;
                        break;
                    }

                    lseek(fd, block2[k]*BLOCK_SIZE, SEEK_SET);
                    read(fd, block3, BLOCK_SIZE);

                    for(l=0;l<BLOCK_SIZE/sizeof(unsigned int);l++)
                    {
                        if(block3[l]==0)
                            continue;
                        if(block3[l]==b1)
                        {
                            block3[l]=b2;
                            k=BLOCK_SIZE;
                            j=BLOCK_SIZE;
                            i=15;
                            break;
                        }
                    }

                    lseek(fd, block2[k]*BLOCK_SIZE, SEEK_SET);
                    write(fd, block3, BLOCK_SIZE);
                }

                lseek(fd, block[j]*BLOCK_SIZE, SEEK_SET);
                write(fd, block2, BLOCK_SIZE);
            }

            lseek(fd, inodeTable[ino].i_block[i]*BLOCK_SIZE, SEEK_SET);
            write(fd, block, BLOCK_SIZE);
        }
    }
}

void defrag(int fd, unsigned int* inodes, unsigned int icnt, unsigned int* btoInode, 
        unsigned int* blockCount, struct ext2_group_desc group, struct ext2_super_block super)
{
    void *addr;
    struct ext2_inode* inodeTable;
    unsigned int curpos, ino;
    int i,j,k,l,m;
    unsigned int offset = group.bg_inode_table*BLOCK_SIZE,
                 paoffs = group.bg_inode_table*BLOCK_SIZE & ~(sysconf(_SC_PAGE_SIZE)-1);
    unsigned int length = super.s_inodes_count*sizeof(struct ext2_inode) + offset-paoffs,
                 *block = malloc(BLOCK_SIZE), *block2 = malloc(BLOCK_SIZE),
                 *block3 = malloc(BLOCK_SIZE);
    bmap *blockmap;

    addr = mmap(NULL, length, PROT_READ|PROT_WRITE, MAP_SHARED, fd, paoffs);
    inodeTable = addr + offset-paoffs - sizeof(struct ext2_inode);//inode numbers start with 1 so subtract to align
    curpos=findFirstEmpty(btoInode);
    for(i=0;i<icnt;i++)
    {
        ino = inodes[i];
        if(ino<12)
            continue;
        
        for(j=0;j<15;j++)
        {
            if(inodeTable[ino].i_block[j]==0)
                continue;
            while(btoInode[curpos]<12 && btoInode[curpos]>0)
                curpos++;
            swapDataBlocks(fd, inodeTable[ino].i_block[j], curpos);
            changeTo(fd, btoInode[curpos], curpos, inodeTable[ino].i_block[j], inodeTable);
            btoInode[inodeTable[ino].i_block[j]] = btoInode[curpos]>ino?btoInode[curpos]:0;
            inodeTable[ino].i_block[j] = curpos;
            btoInode[curpos++] = ino;

            printf("%u %u\n", inodes[i], inodeTable[ino].i_block[j]);
            if(j==12)
            {
                lseek(fd, inodeTable[ino].i_block[j]*BLOCK_SIZE, SEEK_SET);
                read(fd, block, BLOCK_SIZE);

                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block[k]==0)
                        continue;
                    while(btoInode[curpos]<12 && btoInode[curpos]>0)
                        curpos++;
                    swapDataBlocks(fd, block[k], curpos);
                    changeTo(fd, btoInode[curpos], curpos, block[k], inodeTable);
                    btoInode[block[k]] = btoInode[curpos]>ino?btoInode[curpos]:0;
                    block[k] = curpos;
                    btoInode[curpos++] = ino;

                    printf("%u %u\n", ino, block[k]);
                }

                lseek(fd, inodeTable[ino].i_block[j]*BLOCK_SIZE, SEEK_SET);
                write(fd, block, BLOCK_SIZE);
            }
            else if(j==13)
            {
                lseek(fd, inodeTable[ino].i_block[j]*BLOCK_SIZE, SEEK_SET);
                read(fd, block, BLOCK_SIZE);

                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block[k]==0)
                        continue;
                    while(btoInode[curpos]<12 && btoInode[curpos]>0)
                        curpos++;
                    swapDataBlocks(fd, block[k], curpos);
                    changeTo(fd, btoInode[curpos], curpos, block[k], inodeTable);
                    btoInode[block[k]] = btoInode[curpos]>ino?btoInode[curpos]:0;
                    block[k] = curpos;
                    btoInode[curpos++] = ino;

                    printf("%u %u\n", ino, block[k]);

                    lseek(fd, block[k]*BLOCK_SIZE, SEEK_SET);
                    read(fd, block2, BLOCK_SIZE);
                    for(l=0;l<BLOCK_SIZE/sizeof(unsigned int);l++)
                    {
                        if(block2[l]==0)
                            continue;
                        while(btoInode[curpos]<12 && btoInode[curpos]>0)
                            curpos++;
                        swapDataBlocks(fd, block2[l], curpos);
                        changeTo(fd, btoInode[curpos], curpos, block2[l], inodeTable);
                        btoInode[block2[l]] = btoInode[curpos]>ino?btoInode[curpos]:0;
                        block2[l] = curpos;
                        btoInode[curpos++] = ino;

                        printf("%u %u\n", ino, block2[l]);
                    }
                    lseek(fd, block[k]*BLOCK_SIZE, SEEK_SET);
                    write(fd, block2, BLOCK_SIZE);
                }

                lseek(fd, inodeTable[ino].i_block[j]*BLOCK_SIZE, SEEK_SET);
                write(fd, block, BLOCK_SIZE);
            }
            else if(j==14)
            {
                lseek(fd, inodeTable[ino].i_block[j]*BLOCK_SIZE, SEEK_SET);
                read(fd, block, BLOCK_SIZE);

                for(k=0;k<BLOCK_SIZE/sizeof(unsigned int);k++)
                {
                    if(block[k]==0)
                        continue;
                    while(btoInode[curpos]<12 && btoInode[curpos]>0)
                        curpos++;
                    swapDataBlocks(fd, block[k], curpos);
                    changeTo(fd, btoInode[curpos], curpos, block[k], inodeTable);
                    btoInode[block[k]] = btoInode[curpos]>ino?btoInode[curpos]:0;
                    block[k] = curpos;
                    btoInode[curpos++] = ino;

                    printf("%u %u\n", ino, block[k]);

                    lseek(fd, block[k]*BLOCK_SIZE, SEEK_SET);
                    read(fd, block2, BLOCK_SIZE);
                    for(l=0;l<BLOCK_SIZE/sizeof(unsigned int);l++)
                    {
                        if(block2[l]==0)
                            continue;
                        while(btoInode[curpos]<12 && btoInode[curpos]>0)
                            curpos++;
                        swapDataBlocks(fd, block2[l], curpos);
                        changeTo(fd, btoInode[curpos], curpos, block2[l], inodeTable);
                        btoInode[block2[l]] = btoInode[curpos]>ino?btoInode[curpos]:0;
                        block2[l] = curpos;
                        btoInode[curpos++] = ino;

                        printf("%u %u\n", ino, block2[l]);

                        lseek(fd, block2[l]*BLOCK_SIZE, SEEK_SET);
                        read(fd, block3, BLOCK_SIZE);
                        for(m=0;m<BLOCK_SIZE/sizeof(unsigned int);m++)
                        {
                            if(block3[m]==0)
                                continue;
                            while(btoInode[curpos]<12 && btoInode[curpos]>0)
                                curpos++;
                            swapDataBlocks(fd, block3[m], curpos);
                            changeTo(fd, btoInode[curpos], curpos, block3[m], inodeTable);
                            btoInode[block3[m]] = btoInode[curpos]>ino?btoInode[curpos]:0;
                            block3[m] = curpos;
                            btoInode[curpos++] = ino;

                            printf("%u %u\n", ino, block3[m]);
                        }
                        lseek(fd, block2[l]*BLOCK_SIZE, SEEK_SET);
                        write(fd, block3, BLOCK_SIZE);
                    }
                    lseek(fd, block[k]*BLOCK_SIZE, SEEK_SET);
                    write(fd, block2, BLOCK_SIZE);
                }
                lseek(fd, inodeTable[ino].i_block[j]*BLOCK_SIZE, SEEK_SET);
                write(fd, block, BLOCK_SIZE);
            }
        }
    }
    munmap(addr, length);

    offset = group.bg_block_bitmap*BLOCK_SIZE;
    paoffs = group.bg_block_bitmap*BLOCK_SIZE & ~(sysconf(_SC_PAGE_SIZE)-1);
    curpos=findFirstEmpty(btoInode);
    addr = mmap(NULL, BLOCK_SIZE, PROT_READ|PROT_WRITE, MAP_SHARED, fd, paoffs);
    blockmap = addr + offset-paoffs;
    for(i=curpos;i<super.s_blocks_count;i++)
        if(btoInode[i]>0)
            BM_SET(i, blockmap);
        else
            BM_CLR(i, blockmap);

    munmap(addr, BLOCK_SIZE);
}

int main(int argc, char **argv)
{
    struct ext2_super_block super;
    struct ext2_group_desc group;
    unsigned int fd, icnt, *inodes, bcnt, *blocks;
    unsigned int *btoInode, *blockCount;

    fd = open(argv[1], O_RDWR);
    readSuperBlock(fd, &super);
    readGroupBlock(fd, super, 0, &group);
	inodes = getInodes(fd, super, group, &icnt);
    blocks = getBlocks(fd, super, group, &bcnt);
    btoInode = malloc(sizeof(int)*(BLOCK_SIZE*8+1));//one group has at most 8K blocks
    blockCount = malloc(sizeof(int)*(BLOCK_SIZE*8+1));//one group has at most 8K inodes

    markBlocks(fd, inodes, icnt, btoInode, group.bg_inode_table, blockCount);

    puts("#######################");

    defrag(fd, inodes, icnt, btoInode, blockCount, group, super);

    close(fd);
    free(btoInode);
    free(blockCount);
    return 0;
}

