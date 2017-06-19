import sys

if len(sys.argv)<4:
    print "Usage: %s INPUT_FILE OUTPUT TIMEMARGIN(uS)"%sys.argv[0]
    sys.exit(0)

timeMargin = int(sys.argv[3])
f1 = open(sys.argv[2], "r").readlines()
f2 = open(sys.argv[1], "r").readlines()

def parse(lines):
    events = []
    for i in range(len(lines)):
        l = lines[i].split(', ')
        event = {}
        for x in l:
            if ':' not in x:
                continue
            if 'TID' not in x:
                event[x[:x.find(':')]] = int(x[x.find(':')+2:])
            else:
                event[x[:x.find(':')]] = x[x.find(':')+2:]
        events.append(event)
    events = sorted(events, key=lambda x: x['time stamp'])
    base = events[0]['time stamp']
    for e in events:
        e['time stamp'] -= base
    return events

def parseInp(inp):
    Nd,Ns,Nc = map(int, inp[0].split(' '))
    dockCap = map(int, inp[1].split(' '))
    tTime = []
    shipCap = []
    aTime = []
    route = {}
    dockStr = {}
    for i in range(Ns):
        tT, sC, aT, r = inp[2+i].split(' ', 3)
        r = r.split(' ')[1:]
        tTime.append(int(tT)*1000)
        shipCap.append(int(sC))
        aTime.append(int(aT)*1000)
        route[i] = map(int, r)
    for i in range(Nc):
        a,d = map(int, inp[2+Ns+i].split(' '))
        if a not in dockStr:
            dockStr[a] = []
        dockStr[a].append((i,d))

    return dockCap, tTime, shipCap, aTime, route, dockStr

def inputSatisfied(out, inp):
    events = parse(out)
    create = []
    lastActivity = {}
    destroy = []
    entrReq = {}
    docked = {}
    unloadReq = {}
    unload = {}
    loadReq = {}
    load = {}
    leave = {}
    lastLoad = {}
    lastUnload = {}
    dockCap, tTime, shipCap, aTime, route, dockStr = parseInp(inp)
    pos = {}
    shipStr = {}
    for i in range(len(shipCap)):
        shipStr[i] = []
        pos[i] = 0

    for x in events:
        if x['SID'] in lastActivity:
            lastAct = lastActivity[x['SID']]
            if lastAct[0] == 4 and lastAct[1]+2000>x['time stamp']:
                print "Ship should've been unloading"
                print x, lastAct
                break
            if lastAct[0] == 6 and lastAct[1]+3000>x['time stamp']:
                print "Ship should've been loading"
                print x, lastAct
                break
            if lastAct[0] == 0 and aTime[x['SID']]-x['time stamp']>timeMargin:
                print "Ship should've been travelling"
                print x, lastAct, aTime[x['SID']]
                break
            if lastAct[0] == 7 and lastAct[1]+tTime[x['SID']]>x['time stamp']:
                print "Ship should've been travelling"
                print x, lastAct
                break
        if x['AID'] == 0:
            create.append(x['SID'])
            if x['SID'] in lastActivity:
                print "Ship is already created?"
                print x
                break
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 1:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in entrReq:
                entrReq[x['DID']] = []
            for dock in docked:
                if x['SID'] in docked[dock]:
                    print "Ship already in dock!"
                    print x
                    break
            if lastActivity[x['SID']][0] == 0 and -x['time stamp']+aTime[x['SID']] > timeMargin*2:
                print "Ship did not arrive in time"
                print x, lastActivity[x['SID']], aTime[x['SID']]
                break
            if lastActivity[x['SID']][0] != 0 and abs(lastActivity[x['SID']][1]+tTime[x['SID']]-x['time stamp']) > timeMargin*2:
                print "Ship did not arrive in time"
                print x, lastActivity[x['SID']], tTime[x['SID']]
                break
            if route[x['SID']][pos[x['SID']]]!=x['DID']:
                print "Wrong dock :-("
                print x, route[x['SID']], pos[x['SID']]
                break
            pos[x['SID']]+=1
            entrReq[x['DID']].append(x['SID'])
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 2:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in docked:
                docked[x['DID']] = []
            if x['DID'] not in entrReq or x['SID'] not in entrReq[x['DID']]:
                print "Ship did not request entry"
                print x
                break
            if x['DID'] in entrReq and entrReq[x['DID']][0] != x['SID']:
                fail = True
                for ship in entrReq[x['DID']]:
                    if ship == x['SID']:
                        break
                    if (lastActivity[ship][0]==1 and abs(lastActivity[ship][1]-lastActivity[x['SID']][1]) < timeMargin):
                        fail = False
                        break
                if fail:
                    print "Other ship requested first"
                    print x, entrReq[x['DID']]
                    break
            if len(docked[x['DID']])==dockCap[x['DID']]:
                print "Dock capacity exceeded"
                print x
                break
            entrReq[x['DID']].remove(x['SID'])
            docked[x['DID']].append(x['SID'])
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 3: 
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in unloadReq:
                unloadReq[x['DID']] = []
            if x['DID'] not in lastUnload:
                lastUnload[x['DID']] = 0
            if x['DID'] not in docked or x['SID'] not in docked[x['DID']]:
                print "Ship not in dock"
                print x
                break
            for cargo in shipStr[x['SID']]:
                if cargo[1]==x['DID']:
                    break
            else:
                print "Ship does not have any cargo destined to that dock"
                print x, shipStr[x['SID']]
                break
            unloadReq[x['DID']].append(x['SID'])
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 4:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in unload:
                unload[x['DID']] = []
            if x['DID'] not in docked or x['SID'] not in docked[x['DID']]:
                print "Ship not in dock"
                print x
                break
            if x['DID'] not in unloadReq or x['SID'] not in unloadReq[x['DID']]:
                print "Ship did not request unload"
                print x
                break
            if x['DID'] in lastLoad and lastLoad[x['DID']]+3000>=x['time stamp']:
                print "Loading going on on dock"
                print x
                break
            crg=None
            for cargo in shipStr[x['SID']]:
                if cargo[0]==x['CID'] and cargo[1]==x['DID']:
                    crg=cargo
                    break
            else:
                print "Either cargo is not in ship or not destined for that dock"
                print x
                break
            unload[x['DID']].append((x['SID'],x['CID']))
            shipStr[x['SID']].remove(crg)
            lastUnload[x['DID']] = x['time stamp']
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 5:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in lastLoad:
                lastLoad[x['DID']] = 0
            if x['DID'] not in loadReq:
                loadReq[x['DID']] = []
            if x['DID'] not in docked or x['SID'] not in docked[x['DID']]:
                print "Ship not in dock"
                print x
                break
            loadReq[x['DID']].append(x['SID'])
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 6:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in load:
                load[x['DID']] = []
            if x['DID'] not in docked or x['SID'] not in docked[x['DID']]:
                print "Ship not in dock"
                print x
                break
            if x['DID'] not in loadReq or x['SID'] not in loadReq[x['DID']]:
                print "Ship did not request load"
                print x
                break
            if x['DID'] in lastUnload and lastUnload[x['DID']]+2000>=x['time stamp']:
                print "Unloading going on on dock"
                print x
                break
            crg=None
            for cargo in dockStr[x['DID']]:
                if x['CID']==cargo[0]:
                    crg=cargo
                    break
            else:
                print "Cargo not in dock!"
                print x
                break
            if crg[1] not in route[x['SID']][pos[x['SID']]:]:
                print "Destination not en route"
                print x, route[x['SID']], crg
                break
            if len(shipStr[x['SID']])==shipCap[x['SID']]:
                print "Ship capacity exceeded"
                print x
                break
            if lastActivity[x['SID']][0]==5 and -lastActivity[x['SID']][1]+x['time stamp']-20000>timeMargin:
                print "Ship should've left the dock"
                print x, lastActivity[x['SID']]
                break
            shipStr[x['SID']].append(crg)
            dockStr[x['DID']].remove(crg)
            load[x['DID']].append((x['SID'],x['CID']))
            lastLoad[x['DID']] = x['time stamp']
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 7:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['DID'] not in leave:
                leave[x['DID']] = []
            if x['SID'] not in docked[x['DID']]:
                print "Ship not in dock"
                print x
                break
            leave[x['DID']].append(x['SID'])
            docked[x['DID']].remove(x['SID'])
            if x['DID'] in load and x['SID'] in load[x['DID']]:
                load[x['DID']].remove(x['SID'])
            if x['DID'] in unload and x['SID'] in unload[x['DID']]:
                unload[x['DID']].remove(x['SID'])
            lastActivity[x['SID']] = (x['AID'], x['time stamp'])
        elif x['AID'] == 8:
            if x['SID'] not in lastActivity:
                print "Ship is not initated"
                print x
                break
            if x['SID'] not in create:
                print "FAULTY DESTROY"
                print x
                break
            DID = None
            for did in docked:
                if x['SID'] in docked[did]:
                    DID=did
                    break
            if DID != route[x['SID']][-1]:
                print "Ship not in last dock"
                print x
                break
            destroy.append(x['SID'])
            create.remove(x['SID'])
            del lastActivity[x['SID']]
    else:
        for sid in shipStr:
            if len(shipStr[sid])>0:
                print "Ship", sid, "has nonempty storage", shipStr[sid]
                return False
        return True
    return False

if inputSatisfied(f1,f2):
    print "Seems good"

