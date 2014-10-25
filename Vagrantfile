Vagrant.configure("2") do |config|

    config.vm.box = "ubuntu-12.04"
    config.vm.box_url = "http://bit.ly/1elrTrM"

    config.vm.define :babylon do |node|
        node.vm.hostname = :babylon
        node.vm.network :private_network, ip: "192.168.66.66"
        config.vm.network "forwarded_port", guest: 5000, host: 5000
        config.vm.network "forwarded_port", guest: 5050, host: 5050
        config.vm.network "forwarded_port", guest: 27017, host: 27017
        config.vm.network "forwarded_port", guest: 6379, host: 6379

        config.vm.provider :virtualbox do |vb|
            vb.customize ["modifyvm", :id, "--memory", 2048]
            vb.customize ["modifyvm", :id, "--cpus", 2]
        end

        config.vm.provision :shell, inline: <<-eos
            sudo apt-get install -y python-pip mongodb-server redis-server
            sudo pip install --upgrade pip
            sudo pip install -r /vagrant/requirements.txt
            sudo pip install -r /vagrant/requirements-test.txt
            sudo pip install ipython
            sudo gem install foreman -v 0.60
        eos
    end
end


