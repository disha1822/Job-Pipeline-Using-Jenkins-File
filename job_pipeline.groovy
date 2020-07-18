job("task6_job1"){
    description("Download Data From Github And Create Docker Image")
	scm{
		github('disha1822/Test_testing_production_env' , 'master')
	}
	triggers{
		scm(" * * * * * ")
	}
	steps{
		shell('''if sudo test -d /devops_task6
then
sudo rm -rvf /devops_task6
sudo mkdir /devops_task6
sudo cp -vrf * /devops_task6
else
sudo mkdir /devops_task6
sudo cp -vrf * /devops_task6
fi

cd /devops_task6/
sudo docker build -t my-apache2:v5 .
sudo docker tag  my-apache2:v5  disha1822/my-apache2:v5
sudo docker push disha1822/my-apache2:v5
''')
	}

}

job("task6_job2"){
    description("Launching & Monitoring Pods")
	triggers{
		upstream('task6_job1' , 'SUCCESS')
	}
	steps{
		shell('''if sudo kubectl get deployment webapp
then
echo "Deployment already exists"
else
sudo kubectl create -f /devops_task3/web-deploy.yml
fi

''')
	}

}

job("task6_job3"){
    description("Testing Application")
	triggers{
		upstream('task6_job2' , 'SUCCESS')
	}
	steps{
		shell('''status=$(curl -o /dev/null  -s  -w "%{http_code}"  192.168.99.108:30000)

if [ $status == 200 ]
then
exit 1
else
exit 0
fi
''')
	}

}

job("task6_job4"){
    description("Sending Notification If App Fail")
	triggers{
		upstream('task6_job3' , 'SUCCESS')
	}
	steps{
		shell("python3 /mail/mail.py")
	}

}
