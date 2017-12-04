安装：
make

cp redis.conf sentinel.conf ./conf
cp runtest* ./bin
cp mkreleasehdr.sh redis-benchmark redis-check-aof redis-check-dump redis-cli redis-sentinel redis-server redis-trib.rb ../bin/
rm -fr !(bin|conf)


mkdir -p /data3/spark/redis/data
ln -s /data3/spark/redis/data data

/data/apps/redis/bin/redis-server /data/apps/redis/conf/redis.conf 

netstat -nltp | grep 6379
ps aux | grep redis

apt-get update
apt-get install ruby1.9.3
apt-get install rubygems 
gem install redis

/data/apps/redis/bin/redis-trib.rb create 10.21.33.126:6379 10.21.33.125:6379 10.21.33.124:6379 10.21.33.123:6379 10.21.33.122:6379 10.21.33.121:6379 10.21.33.120:6379


/data/apps/redis/bin/redis-cli -c
