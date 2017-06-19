#ifndef PARSER_H
#define PARSER_H

#define MAX_COMMAND_LINE_LENGTH 512
#define MAX_ARG_COUNT 20
#define MAX_ARG_SIZE 128

#define DEBUG 0

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct parsed_command {
    /* Name of the command. This can also be the full path of the executable */
    char *name;
    /* Number of arguments for the command. */
    int num_of_args;
    /* Arguments */
    char **arguments;
} pc; 

typedef enum command_type {
    SUBSHELL,
    NORMAL
} ct;

typedef union subshell_command_union {
    /* Parsed normal command information */
    pc* com; 
    /* Subshell input string. This is a null terminated string with newline at the end. Example: "ls -l \n\0". This is the same as command_line string that parse needs for its input*/
    char *subshell; 
} scu;

typedef struct command {
    /* Command type to determine whether this is a subshell or normal command*/
    ct type; 
    /* Command information union for either normal command or subshell input*/
    scu info;
    /* Input file name if there is any */
    char *input;
    /* Output file name if there is any */
    char *output;
} command;
typedef struct input {
    /* Number of command for this input. */
    int num_of_commands;
    /* Deliminator for multiple command if there is more than one*/
    char del;
    /* Commands */
    command *commands;
    /* Whether or not this is a background execution task */
    int background;
} input;


// command_line must be null terminated string along with its newline. Example string: "ls -l \n\0"
input* parse(char *command_line);
// this will clear memory of the command line to prevent memory leaks. You can use this function after you no longer need the input.
void clear_input(input *);
// this will print information about the parser input
void print_input(input *inp);


























#endif