#include <iostream>
#include <vector>
#include <set>
#include <algorithm>
#include <map>
using namespace std;

struct Post
{
	string uname,msg;

	string str() const
	{
		return "Post \""+uname+"\" \""+msg+'\"';
	}

	void print() const
	{
		cout << "Post \"" << uname << "\" \"" << msg << '\"';
	}

	friend bool operator<(const Post& lhs, const Post& rhs)
	{
		if(lhs.uname!=rhs.uname)
			return lhs.uname<rhs.uname;
		return lhs.msg<rhs.msg;
	}
};

struct To
{
	int type;
	string name;

	string str() const
	{
		string res;
		if(type==0)
			res="UserID";
		else
			res="GroupID";
		return res+" \""+name+"\"";
	}

	void print() const
	{
		if(type==0)
			cout << "UserID";
		else
			cout << "GroupID";
		cout << " \"" << name << "\"";
	}
};

struct User
{
	string uname,rname;
	vector<string> friends;
	set<Post> posts;

	map<string, int> friendRank;

	string str() const
	{
		string res="User \""+uname+"\" \""+rname+"\" [";
		for(int i=0;i<friends.size();i++)
		{
			res+='\"'+friends[i]+'\"';
			if(i!=friends.size()-1)
				res+=", ";
		}
		res+="] [";
		for(auto it=posts.begin();it!=posts.end();it++)
		{
			if(it!=posts.begin())
				res+=", ";
			res+=it->str();
		}
		return res+"]";
	}

	void print() const
	{
		cout << "User \"" << uname << "\" \"" << rname << "\" [";
		for(int i=0;i<friends.size();i++)
		{
			cout << '\"' << friends[i] << '\"';
			if(i!=friends.size()-1)
				cout << ", ";
		}
		cout << "] [";
		for(auto it=posts.begin();it!=posts.end();it++)
		{
			if(it!=posts.begin())
				cout << ", ";
			it->print();
		}
		cout << "]";
	}

	void addFriend(const string &u)
	{
		if(friendRank.find(u)==friendRank.end())
		{
			friendRank[u]=friends.size();
			friends.push_back(u);
		}
	}

	void addPost(const Post& p)
	{
		if(posts.find(p)!=posts.end())
			return;
		posts.insert(p);
	}
};

struct Group
{
	string gname;
	set<string> users;
	set<Post> posts;
	map<string, int> userRank;

	string str() const
	{
		string res="Group \""+gname+"\" [";
		for(auto i=users.begin();i!=users.end();i++)
		{
			if(i!=users.begin())
				res+=", ";
			res+='"'+*i+'"';
		}
		res+="] [";
		for(auto it=posts.begin();it!=posts.end();it++)
		{
			if(it!=posts.begin())
				res+=", ";
			res+=it->str();
		}
		return res+"]";
	}

	void print() const
	{
		cout << "Group \"" << gname << "\" [";
		for(auto i=users.begin();i!=users.end();i++)
		{
			if(i!=users.begin())
				cout << ", ";
			cout << '"' << *i << '"';
		}
		cout << "] [";
		for(auto it=posts.begin();it!=posts.end();it++)
		{
			if(it!=posts.begin())
				cout << ", ";
			it->print();
		}
		cout << "]";
	}

	void addPost(const Post& p)
	{
		posts.insert(p);
	}

	void addMember(string uname)
	{
		users.insert(uname);
	}

	void removeMember(string uname)
	{
		users.erase(uname);
	}
};

struct DB
{
	vector<User> users;
	vector<Group> groups;

	map<string, int> userRank, groupRank;

	string str()
	{
		string res="DB [";
		for(int i=0;i<users.size();i++)
		{
			res+=users[i].str();
			if(i!=users.size()-1)
				res+=", ";
		}
		res+="] [";
		for(int i=0;i<groups.size();i++)
		{
			res+=groups[i].str();
			if(i!=groups.size()-1)
				res+=", ";
		}
		return res+"]";
	}

	void print()
	{
		cout << "DB [";
		for(int i=0;i<users.size();i++)
		{
			users[i].print();
			if(i!=users.size()-1)
				cout << ", ";
		}
		cout << "] [";
		for(int i=0;i<groups.size();i++)
		{
			groups[i].print();
			if(i!=groups.size()-1)
				cout << ", ";
		}
		cout << "]";
	}

	void newUser(const User &u)
	{
/*		cout << "newUser (";
		print();
		cout << ") (";
		u.print();
		cout << ")";
*/		if(userRank.find(u.uname)==userRank.end())
		{
			userRank[u.uname]=users.size();
			users.push_back(u);
		}
	}

	void addFriend(string u1, string u2)
	{
/*		cout << "addFriend (";
		print();
		cout << ") \"" << u1 << "\" \"" << u2 << '\"';
*/		if(userRank.find(u1)!=userRank.end() && userRank.find(u2)!=userRank.end())
		{
			users[userRank[u1]].addFriend(u2);
			users[userRank[u2]].addFriend(u1);
		}
	}

	void sendPost(string sender, string msg, const vector<To>& to)
	{
/*		cout << "sendPost (";
		print();
		cout << ") \"" << sender << "\" \"" << msg << "\" [";
		for(int i=0;i<to.size();i++)
		{
			if(i!=0)
				cout << ", ";
			to[i].print();
		}
		cout << "]";
*/
		Group g;
		Post p;
		p.uname=sender;
		p.msg=msg;
		
		for(int i=0;i<to.size();i++)
			if(to[i].type==0 && userRank.find(to[i].name)!=userRank.end())
				users[userRank[to[i].name]].addPost(p);
			else if(to[i].type==1 && groupRank.find(to[i].name)!=groupRank.end())
			{
				g=groups[groupRank[to[i].name]];
				groups[groupRank[to[i].name]].addPost(p);
				for(auto j=g.users.begin();j!=g.users.end();j++)
					users[userRank[*j]].addPost(p);
			}
	}

	void newGroup(string gname)
	{
/*		cout << "newGroup (";
		print();
		cout << ") \"" << gname << "\"";
*/
		Group g;
		if(groupRank.find(gname)==groupRank.end())
		{
			groupRank[gname]=groups.size();
			g.gname=gname;
			groups.push_back(g);
		}
	}

	void addMember(string gname, string uname)
	{
/*		cout << "addMember (";
		print();
		cout << ") \"" << gname << "\" \"" << uname << "\"";
*/
		if(groupRank.find(gname)!=groupRank.end())
			groups[groupRank[gname]].addMember(uname);
	}

	void removeMember(string gname, string uname)
	{
		/*cout << "removeMember (";
		print();
		cout << ") \"" << gname << "\" \"" << uname << "\"";*/

		if(groupRank.find(gname)!=groupRank.end())
			groups[groupRank[gname]].removeMember(uname);
	}

	vector<string> getFriendNames(string uname)
	{
		User u;
		vector<string> res;
		if(userRank.find(uname)!=userRank.end())
		{
			u=users[userRank[uname]];
			for(int i=0;i<u.friends.size();i++)
				res.push_back(users[userRank[u.friends[i]]].rname);
		}

		/*cout << "getFriendNames (";
		print();
		cout << ") \"" << uname << "\"";*/

		return res;
	}

	set<Post> getPosts(To to)
	{
		set<Post> res;
		if(to.type==0 && userRank.find(to.name)!=userRank.end())
			res=users[userRank[to.name]].posts;
		else if(to.type==1 && groupRank.find(to.name)!=groupRank.end())
			res=groups[groupRank[to.name]].posts;

		/*cout << "getPosts (";
		print();
		cout << ") (";
		to.print();
		cout << ")";*/

		return res;
	}

	vector<Group> listGroups(string uname)
	{
		vector<Group> res;
		if(userRank.find(uname)!=userRank.end())
			for(int i=0;i<groups.size();i++)
				if(groups[i].users.find(uname)!=groups[i].users.end())
					res.push_back(groups[i]);
		
		/*cout << "listGroups (";
		print();
		cout << ") \"" << uname << "\"";*/

		return res;
	}
	
	bool isFriends(string u1, string u2)
	{
		return users[userRank[u1]].friendRank.find(u2)!=users[userRank[u1]].friendRank.end();
	}

	int countCom(const vector<string>& a, const vector<string>& b)
	{
		map<string, bool> exist;
		for(int i=0;i<a.size();i++)
			exist[a[i]]=true;
		int res=0;
		for(int i=0;i<b.size();i++)
			res+=exist.find(b[i])!=exist.end();
		return res;
	}

	vector<User> suggestFriends(const User& u, int lim)
	{
		vector<User> res;

		/*cout << "suggestFriends (";
		print();
		cout << ") (";
		u.print();
		cout << ") " << lim;*/

		if(userRank.find(u.uname)==userRank.end())
			return res;

		for(int i=0;i<users.size();i++)
		{
			if(countCom(u.friends, users[i].friends)>=lim 
					&& u.uname!=users[i].uname && u.friendRank.find(users[i].uname)==u.friendRank.end())
				res.push_back(users[i]);
		}

		return res;
	}
};

template<typename T>
string str(const vector<T>& x)
{
	string res="[";
	for(int i=0;i<x.size();i++)
	{
		if(i>0)
			res+=", ";
		res+=x[i].str();
	}
	return res+"]";
}

template<typename T>
string str(const set<T>& x)
{
	string res="[";
	for(auto i=x.begin();i!=x.end();i++)
	{
		if(i!=x.begin())
			res+=", ";
		res+=i->str();
	}
	return res+"]";
}

template<>
string str<string>(const vector<string>& x)
{
	string res="[";
	for(int i=0;i<x.size();i++)
	{
		if(i>0)
			res+=", ";
		res+='"'+x[i]+'"';
	}
	return res+"]";
}

template<typename T>
void print(const vector<T>& x)
{
	cout << "[";
	for(int i=0;i<x.size();i++)
	{
		if(i>0)
			cout << ", ";
		x[i].print();
	}
	cout << "]" << endl;
}

template<typename T>
void print(const set<T>& x)
{
	cout << "[";
	for(auto i=x.begin();i!=x.end();i++)
	{
		if(i!=x.begin())
			cout << ", ";
		i->print();
	}
	cout << "]" << endl;
}

template<>
void print<string>(const vector<string>& x)
{
	cout << "[";
	for(int i=0;i<x.size();i++)
	{
		if(i>0)
			cout << ", ";
		cout << '"' << x[i] << '"';
	}
	cout << "]" << endl;
}

string genStr(int n=-1)
{
	string res;
	if(n==-1)
		n=rand()%32+1;
	for(int i=0;i<n;i++)
		res+=(char)(rand()%26+'a');
	return res;
}

int main()
{
	srand(time(NULL));
	DB db;
	
	map<string,bool> isuser,isgroup;
	vector<string> users,groups;
	int nusers=100,ncom=1000;
	int ngroups=100,nqu=1000;
	int mtosize=32;

	for(int i=0;i<nusers;i++)
	{
		string a=genStr();
		while(isuser.find(a)!=isuser.end())
			a=genStr();
		isuser[a]=true;
		users.push_back(a);
	}
	for(int i=0;i<ngroups;i++)
	{
		string a=genStr();
		while(isgroup.find(a)!=isgroup.end())
			a=genStr();
		isgroup[a]=true;
		groups.push_back(a);
	}

	string dbs="DB [] []";
	User u;
	for(int i=0;i<nusers;i++)
	{
		u.uname=u.rname=users[i];
		u.rname+='R';
		dbs = "newUser ("+dbs+") (User \""+u.uname+"\" \""+u.rname+"\" [] [])";
		db.newUser(u);
	}
	for(int i=0;i<ngroups;i++)
	{
		dbs = "newGroup ("+dbs+") \""+groups[i]+"\"";
		db.newGroup(groups[i]);
	}
	for(int i=0;i<ncom;i++)
	{
		int c=rand()%6;
		if(c==0)
		{
			string name=genStr();
			while(isuser.find(name)!=isuser.end())
				name=genStr();
			isuser[name]=true;
			users.push_back(name);
			u.uname=u.rname=name;
			u.rname+='R';
			dbs = "newUser ("+dbs+") (User \""+u.uname+"\" \""+u.rname+"\" [] [])";
			db.newUser(u);
		}
		else if(c==1)
		{
			int u1,u2;
			u1=rand()%users.size();
			u2=rand()%users.size();
			while(u1==u2 || db.isFriends(users[u1], users[u2])) 
				u2=rand()%users.size();
			dbs = "addFriend ("+dbs+") \""+users[u1]+"\" \""+users[u2]+"\"";
			db.addFriend(users[u1], users[u2]);
		}
		else if(c==2)
		{
			Post p;
			string msg=genStr();
			int sender=rand()%users.size();
			vector<To> tos;
			To to;
			p.uname=users[sender];
			p.msg=msg;
			tos.resize(rand()%mtosize);
			for(int i=0;i<tos.size();i++)
			{
				to.type=rand()%2;
				if(to.type==0)
					to.name=users[rand()%users.size()];
				else
					to.name=groups[rand()%groups.size()];
				tos[i]=to;
			}
			dbs = "sendPost ("+dbs+") \""+users[sender]+"\" \""+msg+"\" "+str(tos);
			db.sendPost(users[sender], msg, tos);
		}
		else if(c==3)
		{
			string name=genStr();
			while(isgroup.find(name)!=isgroup.end())
				name=genStr();
			isgroup[name]=true;
			groups.push_back(name);
			dbs = "newGroup ("+dbs+") \""+name+'"';
			db.newGroup(name);
		}
		else if(c==4)
		{
			int g=rand()%groups.size(),u=rand()%users.size();
			dbs = "addMember ("+dbs+") \""+groups[g]+"\" \""+users[u]+"\"";
			db.addMember(groups[g], users[u]);
		}
		else if(c==5)
		{
			int g=rand()%groups.size(),u=rand()%users.size();
			dbs = "removeMember ("+dbs+") \""+groups[g]+"\" \""+users[u]+"\"";
			db.removeMember(groups[g], users[u]);
		}
	}

	cout << dbs << endl;

	for(int i=0;i<nqu;i++)
	{
		int q=rand()%4;
		if(q==0)
		{
			int u=rand()%users.size();
			cout << "getFriendNames (db) \""+users[u]+"\"" << endl;
			print(db.getFriendNames(users[u]));
		}
		else if(q==1)
		{
			To to;
			to.type=rand()%2;
			if(to.type==0)
				to.name=users[rand()%users.size()];
			else
				to.name=groups[rand()%groups.size()];
			cout << "getPosts (db) ("+to.str()+")" << endl;
			print(db.getPosts(to));
		}
		else if(q==2)
		{
			int u=rand()%users.size();
			cout << "listGroups (db) \""+users[u]+'"' << endl;
			print(db.listGroups(users[u]));
		}
		else if(q==3)
		{
			int u=rand()%users.size(),lim=rand()%5;
			cout << "suggestFriends (db) (";
			db.users[u].print();
			cout << ") " << lim << endl;
			print(db.suggestFriends(db.users[u], lim));
		}
	}
	
	return 0;
}

