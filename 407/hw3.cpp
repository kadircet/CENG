#include <iostream>
#include <iomanip>
using namespace std;

double f[101][51];

int main()
{
	f[1][1]=1;
	f[2][1]=1;
	for(int i=3;i<101;i++)
		for(int j=1;j<=i;j++)
			f[i][j]=(f[i-1][j]*(i-1-j)+f[i-1][j-1]*(j-1))/(i-1);
	for(int i=2;i<101;i+=2)
		cout << std::setprecision(51) << f[i][i/2] << endl;

	return 0;
}
