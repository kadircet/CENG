#include <stdio.h>
#include <stdlib.h>

#include "parser.h"

#define START_PHASE 1

#define SUBSHELL_START 2
#define SUBSHELL_READING 3

#define COMMAND_NAME_READING 4
#define COMMAND_ARGUMENT_READING 5 

#define COMMAND_AFTER 6
#define COMMAND_AFTER_REDIRECT 7

#define COMMAND_INPUT_START 8
#define COMMAND_INPUT_READING 9

#define COMMAND_OUTPUT_START 10
#define COMMAND_OUTPUT_READING 11



#define END_PHASE 100

void clear_pc(pc *command) {
    if ( !command )
        return;
    if ( command->name )
        free(command->name);
    for ( int i=0 ; i<command->num_of_args ; i++ ) {
        free(command->arguments[i]);
    }
    if ( command->arguments )
        free(command->arguments);
    free(command);
}

void clear_command(command *command) {
    if ( !command )
        return;

    if ( command->input )
        free(command->input);
    if ( command->output )
        free(command->output);

    if ( command->type == NORMAL ) {
        clear_pc(command->info.com);
    }
    else {
        if ( command->info.subshell )
            free(command->info.subshell);
    }
}

void clear_input(input *inp) {
    if ( !inp )
        return;
    for ( int i=0 ; i<inp->num_of_commands ; i++ )
        clear_command(inp->commands+i);

    free(inp->commands);
    
    free(inp);
}

void print_pc(pc* command) {
    if ( !command )
        return;
    if ( command->name )
        printf("Name: %s \n", command->name);
    if ( command->num_of_args ) {
        printf("Number of Arguments: %d \n", command->num_of_args);
        for ( int i=0 ; i<command->num_of_args ; i++ ) {
            printf("%d. Argument: %s\n", i+1, command->arguments[i]);
        }
    }
}

void print_command(command *command, int index) {
    if ( !command )
        return;
    printf("%d. ", index+1);
    if ( command->type == NORMAL ) {
        printf("Normal Command \n");
        print_pc(command->info.com);
    }
    else {
        printf("Subshell Command\n");
        printf("Content: %s", command->info.subshell);
    }
    if ( command->input )
        printf("Input File: %s \n", command->input);
    if ( command->output )
        printf("Output File: %s \n", command->output);
}

void print_input(input *inp) {
    if ( !inp )
        return;
    printf("============================================================\n");
    
    if ( inp->background )
        printf("Background Execution\n");
    else
        printf("Foreground Execution\n");
    printf("Number of Commands:%d \n", inp->num_of_commands);
    if ( inp->num_of_commands > 1 )
        printf("Deliminator:%c \n", inp->del);
    for ( int i=0 ; i<inp->num_of_commands ; i++ ) {
        printf("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
        print_command(inp->commands+i, i);
    }
    printf("============================================================\n");
}

void expand_commands(input* result) {
    command *old_commands = result->commands;
    
    result->num_of_commands++;
    result->commands = (command *)calloc(result->num_of_commands, sizeof(command));
    
    if ( old_commands ) {
        memcpy(result->commands, old_commands, sizeof(command)*(result->num_of_commands-1));
        
        free(old_commands);
    }
}

void expand_arguments(pc* command) {
    char **old_args = command->arguments;
    
    command->num_of_args++;
    command->arguments = (char **)calloc(command->num_of_args, sizeof(char*));
    
    if ( old_args ) {
        memcpy(command->arguments, old_args, sizeof(char*)*(command->num_of_args-1));
        
        free(old_args);
    }
}

void add_subshell(input* result, char buffer[], int buffer_size) {
    expand_commands(result);
    
    result->commands[result->num_of_commands-1].type = SUBSHELL;
    result->commands[result->num_of_commands-1].info.subshell = (char *)malloc(sizeof(char)*buffer_size);
    
    strncpy(result->commands[result->num_of_commands-1].info.subshell, buffer, buffer_size);
}

void add_command_name(input* result, char buffer[], int buffer_size) {
    expand_commands(result);
    
    result->commands[result->num_of_commands-1].type = NORMAL;
    result->commands[result->num_of_commands-1].info.com = (pc*)calloc(1, sizeof(pc));
    result->commands[result->num_of_commands-1].info.com->name = (char *)malloc(sizeof(char)*buffer_size);
    
    strncpy(result->commands[result->num_of_commands-1].info.com->name, buffer, buffer_size);
}

void add_command_argument(input* result, char buffer[], int buffer_size) {
    pc* command = result->commands[result->num_of_commands-1].info.com;
    expand_arguments(command);
    
    command->arguments[command->num_of_args-1] = (char *)malloc(sizeof(char)*buffer_size);
    
    strncpy(command->arguments[command->num_of_args-1], buffer, buffer_size);
}

void add_command_input(input* result, char buffer[], int buffer_size) {
    result->commands[result->num_of_commands-1].input = (char *)malloc(sizeof(char)*buffer_size);
    
    strncpy(result->commands[result->num_of_commands-1].input, buffer, buffer_size);
}

void add_command_output(input* result, char buffer[], int buffer_size) {
    result->commands[result->num_of_commands-1].output = (char *)malloc(sizeof(char)*buffer_size);
    
    strncpy(result->commands[result->num_of_commands-1].output, buffer, buffer_size);
}

input* parse(char *command_line) {
    
    input *result = (input*)calloc(1, sizeof(input));
    
    int phase = START_PHASE;
    char temp;
    
    char buffer[MAX_ARG_SIZE+1];
    int buffer_index;
    char *ptr = command_line;
    
    while( (temp=(*ptr++)) != '\0' ) {
        if ( phase == END_PHASE )
            continue;
        if ( phase == START_PHASE ) {
            if ( temp == ' ' )
                continue;
            else if ( temp == '(' ) {
                phase = SUBSHELL_START;
            }
            else if ( temp == ';' || temp == '|' )
                result->del = temp;
            else if ( temp == '&' ) {
                result->background = 1;
                phase = END_PHASE;
            }
            else {
                phase = COMMAND_NAME_READING;
                buffer_index = 0;
                buffer[buffer_index++] = temp;
            }
        }
        else if ( phase == SUBSHELL_START ) {
            if ( temp == ' ' )
                continue;
            else {
                phase = SUBSHELL_READING;
                buffer_index = 0;
                buffer[buffer_index++] = temp;
            }
        }
        else if ( phase == SUBSHELL_READING ) {
            if ( temp == ')' ) {
                buffer[buffer_index++] = '\n';
                buffer[buffer_index++] = '\0';
                add_subshell(result, buffer, buffer_index);
                phase = COMMAND_AFTER_REDIRECT;
            }
            else {
                buffer[buffer_index++] = temp;
            }
        }
        else if ( phase == COMMAND_NAME_READING ) {
            if ( temp == ' ' || temp == '\n' ) {
                buffer[buffer_index++] = '\0';
                add_command_name(result, buffer, buffer_index);
                phase = COMMAND_AFTER;
            }
            else
                buffer[buffer_index++] = temp;
        }
        else if ( phase == COMMAND_AFTER ) {
            if ( temp == ' ' || temp == '\n' )
                continue;
            else if ( temp == '<' )
                phase = COMMAND_INPUT_START;
            else if ( temp == '>' )
                phase = COMMAND_OUTPUT_START;
            else if ( temp == '|' || temp == ';' ) {
                result->del = temp;
                phase = START_PHASE;
            }
            else if ( temp == '&' ) {
                result->background = 1;
                phase = END_PHASE;
            }
            else {
                phase = COMMAND_ARGUMENT_READING;
                buffer_index = 0;
                buffer[buffer_index++] = temp;
            }
        }
        else if ( phase == COMMAND_AFTER_REDIRECT ) {
            if ( temp == ' ' || temp == '\n' )
                continue;
            else if ( temp == '<' )
                phase = COMMAND_INPUT_START;
            else if ( temp == '>' )
                phase = COMMAND_OUTPUT_START;
            else if ( temp == '|' || temp == ';' ) {
                result->del = temp;
                phase = START_PHASE;
            }
            else if ( temp == '&' ) {
                result->background = 1;
                phase = END_PHASE;
            }
        }
        else if ( phase == COMMAND_ARGUMENT_READING ) {
            if ( temp == ' ' || temp == '\n' ) {
                buffer[buffer_index++] = '\0';
                add_command_argument(result, buffer, buffer_index);
                phase = COMMAND_AFTER;
            }
            else
                buffer[buffer_index++] = temp;
        }
        else if ( phase == COMMAND_INPUT_START ) {
            if ( temp == ' ')
                continue;
            else {
                phase = COMMAND_INPUT_READING;
                buffer_index = 0;
                buffer[buffer_index++] = temp;
            }
        }
        else if ( phase == COMMAND_INPUT_READING ) {
            if ( temp == ' ' || temp == '\n' ) {
                buffer[buffer_index++] = '\0';
                add_command_input(result, buffer, buffer_index);
                phase = COMMAND_AFTER_REDIRECT;
            }
            else
                buffer[buffer_index++] = temp;
        }
        else if ( phase == COMMAND_OUTPUT_START ) {
            if ( temp == ' ' )
                continue;
            else {
                phase = COMMAND_OUTPUT_READING;
                buffer_index = 0;
                buffer[buffer_index++] = temp;
            }
        }
        else if ( phase == COMMAND_OUTPUT_READING ) {
            if ( temp == ' ' || temp == '\n' ) {
                buffer[buffer_index++] = '\0';
                add_command_output(result, buffer, buffer_index);
                phase = COMMAND_AFTER_REDIRECT;
            }
            else
                buffer[buffer_index++] = temp;
        }
    }
    
    return result;
}