import random

class worldObject:
  def __init__(self, name, location):
    self.name = name.lower()
    self.location = location
    self.has = []

  def __repr__(self):
      return self.name


class Action:
  def act(self, objects, locations, actors):
    pass
  
class Move(Action):
  def act(self, objects, locations, actors):
    for actor in actors:
      if objects[0] in actor.has:
        actor.has.remove(objects[0])
    objects[0].location = locations[0]
    actors[0].location = locations[0]

    return actors[0].name + " " + objects[0].name + "yi " + objects[0].location.name + "a tasidi ."

class Get(Action):
  def act(self, objects, locations, actors):
    if objects[0] in actors[0].has:
      return None
    for actor in actors:
      if objects[0] in actor.has:
        actor.has.remove(objects[0])
    actors[0].has.append(objects[0])
    actors[0].location = objects[0].location

    return actors[0].name + " " + objects[0].name + "yi " + objects[0].location.name + "dan aldi ."

class Drop(Action):
  def act(self, objects, locations, actors):
    if objects[0] not in actors[0].has:
      return None
    objects[0].location = actors[0].location
    actors[0].has.remove(objects[0])

    return actors[0].name + " " + objects[0].name + "yi " + objects[0].location.name + "a birakti ."

class Go(Action):
  def act(self, objects, locations, actors):
    actors[0].location = locations[0]
    for obj in actors[0].has:
      obj.location = actors[0].location

    return actors[0].name + " " + actors[0].location.name + "a gitti ."

class World:
  def __init__(self):
    locations = [worldObject(x, None) for x in open("dataset/locations.txt").read().split('\n')[:-1]]
    actors = open("dataset/actors.txt").read().split('\n')[:-1]
    self.actors = [worldObject(x, y) for x,y in zip(actors, random.sample(locations, len(actors)))]
    things = open("dataset/things.txt").read().split('\n')[:-1]
    self.things = [worldObject(x, y) for x,y in zip(things, random.sample(locations, len(things)))]
    self.locations = locations

  def genQuery(self, obj):
    return obj.name +" nerede ?," + obj.location.name

  def genStory(self, slen=-1):
    for actor in self.actors:
      actor.has = []
    sLen = slen
    if slen==-1:
      sLen = random.randint(1,5)
    acts = [Move(), Get(), Drop(), Go()]
    story = []
    objs = []
    for _ in xrange(sLen):
      thing = random.sample(self.things, 1)
      actor = random.sample(self.actors, 1)
      loc   = random.sample(self.locations, 1)
      action = random.randint(0,len(acts)-1)
      res = acts[action].act(thing, loc, actor+self.actors)
      while res==None:
        action = action+1
        if action==len(acts):
          action = 0
        res = acts[action].act(thing, loc, actor+self.actors)
      story.append(res)
      if action<3:
          objs.append(thing[0])
      objs.append(actor[0])
    objs = sorted(set(objs))
    queries = []
    for obj in objs:
      queries.append(self.genQuery(obj))
    random.shuffle(queries)
    story = story + queries
    return story


