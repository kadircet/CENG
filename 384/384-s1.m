function ret = createSineWave (f, fs, T)
 dt = 1/fs;
 ret = [0:dt:T-dt];
 ret = sin (2*pi*f*ret);
end

function ret = createSquareWave (f, fs, T)
 sineWave = createSineWave (f, fs, T);
 ret = sign (sineWave);
end

function ret = createSawWave (f, fs, T)
 N = 1/f;
 dt = 1/fs;
 ret = [0:dt:T-dt];
 ret = 2 * (ret ./ N - floor (ret ./ N - .5) - 1);
end

function ret = createTriangleWave (f, fs, T)
 sawWave = createSawWave (f, fs, T);
 ret = abs (sawWave);
end

function [sine, sq, saw, tri] = createWaves (f, fs, T)
 sine    = createSineWave     (f, fs, T);
 sq      = createSquareWave   (f, fs, T);
 saw     = createSawWave      (f, fs, T);
 tri     = createTriangleWave (f, fs, T);
end

function ret = createAbsSineWave (f, fs, T)
 sineWave = createSineWave (f, fs, T);
 ret = abs (sineWave);
end

function ret = delay (signal, d, n)
 ret = signal;
 for i=[1:size(signal,2)]
  for j=[1:n]
   if i-j*d <= 0
     break
   end
   ret (i) += signal (i-j*d);
  end
 end
end

function [t, ret] = construct (n, c_0, c_k, L, fs, T)
 dt = 1/fs;
 t = [0:dt:T-dt];
 ret = zeros (1, floor(T/dt));
 ret += c_0;
 for j=[1:n]
  ret += c_k( j) * exp ( i*j*pi/L*t);
  ret += c_k(-j) * exp (-i*j*pi/L*t);
 end
end

function ret = f(k, x)
  fr = 2;
  ret = exp(2i*pi*fr*k*x);
end

function ret = g(n, x)
  ret = 0;
  for i=[0:n]
    ret += f(i, x);
  end
end
