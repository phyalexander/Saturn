
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


# n = ARGV[0].to_i
# m = ARGV[1].to_i
# make_benchfile n, m

# time = Time::now
# system "java -jar ../jars/Saturn.jar #{BENCH_FILE}" if n <= 1000 && m <= 1000
# took = Time::now - time
# puts "Scala Saturn took time: #{took} sec"

# time = Time::now
# system "./saturn_vm buba_boba.json"
# took = Time::now - time
# puts "Rust Saturn took time: #{took} sec"

# time = Time::now
# system "java -jar Sum.jar #{n} #{m}"
# took = Time::now - time
# puts "Scala Sum took time: #{took} sec"



Data = [[], [], []]
Ns = [10, 100, 200, 300, 400] # 500, 600, 700, 800, 900, 1000, 2000, 4000, 8000, 16000, 50000, 100000]

for n in Ns do 
  make_benchfile n, n
  if n <= 500 then
    time = Time::now
    system "java -jar ../jars/Saturn.jar run #{BENCH_FILE}"
    took = Time::now - time
    Data[0].push took
    puts "Scala Saturn took time: #{took} sec"
  end
  
  system "java -jar ../jars/Saturn.jar compile -json #{BENCH_FILE}"
  time = Time::now
  system "./saturn_vm buba_boba.json"
  took = Time::now - time
  Data[1].push took
  puts "Rust Saturn took time: #{took} sec"
  
  time = Time::now
  system "java -jar Sum.jar #{n} #{n}"
  took = Time::now - time
  Data[2].push took
  puts "Scala Sum took time: #{took} sec"
end

