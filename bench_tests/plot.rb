require 'pycall'
Plt = PyCall.import_module "matplotlib.pyplot"


def encode(n)
	str = '1'
	while n > 1 do
		str = 's(' + str + ')'
		n -= 1
	end
	str
end


def decode(str)
	str.count(')') + 1
end


HEADER = """
X : Any
X + 1 -> s(X)

X : Any, Y : Any
X + s(Y) -> s(X + Y)
"""

BENCH_FILE = 'bench.saturn'


def make_benchfile(n, m)
	File::open(BENCH_FILE, 'w') do |file|
		file.write(HEADER)
		file.write("\n")
		file.write('task: ' + encode(n) + ' + ' + encode(m) + "\n")
	end
end



data = {scala: [], rust: [], sum: []}
ns = [10, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 2000, 3000, 5000, 7000, 10000]

for n in ns do 
	make_benchfile n, n
	if n <= 500 then
		time = Time::now
		system "java -jar ../jars/Saturn.jar run #{BENCH_FILE}"
		took = Time::now - time
		data[:scala].push took
		puts "Scala Saturn took time: #{took} sec"
	end

	system "java -jar ../jars/Saturn.jar compile -json #{BENCH_FILE}"
	time = Time::now
	system "./saturn_vm #{BENCH_FILE.sub(".saturn", ".json")}"
	took = Time::now - time
	data[:rust].push took
	puts "Rust Saturn took time: #{took} sec"

	time = Time::now
	system "java -jar Sum.jar #{n} #{n}"
	took = Time::now - time
	data[:sum].push took
	puts "Scala Sum took time: #{took} sec"
end

data[:scala] += ([0.0/0.0] * (ns.size - data[:scala].size))

puts "n\tscala saturn\trust saturn\tscala sum"
for i in 0...ns.size do
	puts "#{ns[i]}\t#{data[:scala][i]}\t#{data[:rust][i]}\t#{data[:sum][i]}"
end


Plt.xlabel 'n for calc(n + n)'
Plt.ylabel 'time, sec'
Plt.plot ns, data[:scala], label: "scala saturn", color: "orange"
Plt.plot ns, data[:rust],  label: "rust saturn",  color: "green"
Plt.plot ns, data[:sum],   label: "scala sum",    color: "blue"
Plt.yscale ARGV[0].nil? ? 'linear' : ARGV[0]
Plt.legend()
Plt.grid true
Plt.show


'''
n		scala saturn	rust saturn			scala sum
10		0.631336		0.014793			0.220185
100		0.516879		0.008776			0.233722
200		1.295207		0.010753			0.170749
300		4.428234		0.020002			0.177803
400		9.277377		0.029353			0.181022
500		15.180981		0.064067			0.231754
600		NaN				0.053171			0.174845
700		NaN				0.068453			0.172538
800		NaN				0.086778			0.17605
900		NaN				0.10566				0.179537
1000	NaN				0.126517			0.171995
2000	NaN				0.486515			0.170233
3000	NaN				1.070504			0.170551
5000	NaN				3.155226			0.182003
7000	NaN				6.291075			0.266896
10000	NaN				31.483364			0.17934
'''
