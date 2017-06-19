#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <stack>
#include <unordered_map>
using namespace std;

vector<vector<int> > dfa[2];
unordered_map<int,bool> finals[2];
int start[2];
vector<int> parent, _rank;
vector<bool> hasfinal;
stack<pair<int,int> > cur;

int find(int x)
{
	if(parent[x]!=x)
		parent[x]=find(parent[x]);
	return parent[x];
}

void merge(int x, int y)
{
	x=find(x);
	y=find(y);
	if(x==y)
		return;

	if(_rank[x]<_rank[y])
		parent[x]=y;
	else if(_rank[x]>_rank[y])
		parent[y]=x;
	else
	{
		parent[y]=x;
		_rank[x]++;
	}
}

int main(int argc, char **argv)
{
	int N,M,x,y;
	int s1,s2;
	string line;
	stringstream ss;
	fstream file;

	for(int f=1;f<3;f++)
	{
		file.open(argv[f]);
		file >> N >> M >> start[f-1];
		file.ignore(100, '\n');
		getline(file, line);
		ss.str(line);
		//cout << "finals: " << line << endl;
		while(!ss.eof())
		{
			ss >> x;
			if(finals[f-1].find(x)==finals[f-1].end())
				finals[f-1][x]=true;
			//cout << x << " is final" << endl;
		}
		ss.clear();
		dfa[f-1].resize(N);
		for(int i=0;i<N;i++)
			for(int j=0;j<M;j++)
			{
				file >> x;
				dfa[f-1][i].push_back(x);
			}
		file.close();
	}
	s1=dfa[0].size();
	s2=dfa[0][0].size();
	if(s2!=dfa[1][0].size())
	{
		cout << "unequal" << endl;
		return 0;
	}

	parent.resize(s1+dfa[1].size());
	hasfinal.resize(parent.size());
	_rank.resize(parent.size());
	for(int i=0;i<parent.size();i++)
	{
		_rank[i]=0;
		parent[i]=i;
	}

	cur.push({start[0],start[1]});
	while(!cur.empty())
	{
		pair<int,int> top = cur.top();
		cur.pop();
		for(int i=0;i<s2;i++)
		{
			x=dfa[0][top.first][i];
			y=dfa[1][top.second][i];
			if(find(x)!=find(y+s1))
			{
				//cout << "merged " << x << ' ' << y << endl;
				merge(x,y+s1);
				cur.push({x,y});
			}
		}
	}

	for(auto it=finals[0].begin();it!=finals[0].end();it++)
		hasfinal[find(it->first)]=true;
	for(auto it=finals[1].begin();it!=finals[1].end();it++)
		hasfinal[find(it->first+s1)]=true;
	for(int i=0;i<s1;i++)
		if(finals[0].find(i)==finals[0].end() && hasfinal[find(i)])
		{
			//cout << i << " of the first" << endl;
			cout << "unequal" << endl;
			return 0;
		}
	for(int i=s1;i<parent.size();i++)
		if(finals[1].find(i-s1)==finals[1].end() && hasfinal[find(i)])
		{
			//cout << (i-s1) << " of the second" << endl;
			cout << "unequal" << endl;
			return 0;
		}

	cout << "equal" << endl;
	return 0;
}

