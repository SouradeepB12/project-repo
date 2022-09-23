		/*importing the required AWS EC2 packages*/
package aws;
import java.util.Scanner;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressRequest;
import com.amazonaws.services.ec2.model.RevokeSecurityGroupEgressResult;
import com.amazonaws.services.ec2.model.SecurityGroup;


		/*Starting the class to create EC2 security group and authorize ingress and egress rules*/
public class SecGroup {

	public static void main(String[] args) {

        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (C:\\Users\\sbhatta\\.aws\\credentials).
         */
		
		/*Input security group name,group id, and vpc id*/
		Scanner obj = new Scanner(System.in);  
        System.out.println("Provide Security Group Name:");
        String group_name = obj.nextLine();
        System.out.println("Provide Security Group Description:");
        String group_desc = obj.nextLine();
        System.out.println("Provide VPC ID:");
        String vpc_id = obj.nextLine();
        
        /*Step-2: Creating AWS EC2 client*/
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
        
        /*Step-3: Creating AWS EC2 security group*/
        CreateSecurityGroupRequest create_request = new
                CreateSecurityGroupRequest()
                    .withGroupName(group_name)
                    .withDescription(group_desc)
                    .withVpcId(vpc_id);
        
        CreateSecurityGroupResult create_response =
                ec2.createSecurityGroup(create_request);
        
        System.out.println("Successfully created security group named: " + group_name);
        
        /*Step-4: Retrieving security group default egress IpPermissions and group id*/
        String group_id = create_response.getGroupId();
        
        IpRange ip_range_rev = new IpRange()
                .withCidrIp("0.0.0.0/0");

        IpPermission ip_perm4 = new IpPermission()
        		.withIpProtocol("-1")
                .withIpv4Ranges(ip_range_rev);
        
        /*Step-5: Revoking the security group default egress IpPermissions*/
        RevokeSecurityGroupEgressRequest rev_request = new
                RevokeSecurityGroupEgressRequest()
                    .withGroupId(group_id)
                    .withIpPermissions(ip_perm4);
       
        
        RevokeSecurityGroupEgressResult rev_response =
                ec2.revokeSecurityGroupEgress(rev_request);
        
        System.out.println("Default egress rules revoked from security group: " + group_name);
                
        /*Step-6: Authorizing new security group ingress rules*/
        IpRange ip_range_in = new IpRange()
                .withCidrIp("129.186.0.0/16");

            IpPermission ip_perm1 = new IpPermission()
                .withIpProtocol("tcp")
                .withToPort(80)
                .withFromPort(80)
                .withIpv4Ranges(ip_range_in);

            IpPermission ip_perm2 = new IpPermission()
                .withIpProtocol("tcp")
                .withToPort(22)
                .withFromPort(22)
                .withIpv4Ranges(ip_range_in);

            AuthorizeSecurityGroupIngressRequest auth_request = new
                AuthorizeSecurityGroupIngressRequest()
                    .withGroupName(group_name)
                    .withIpPermissions(ip_perm1, ip_perm2);

            AuthorizeSecurityGroupIngressResult auth_response =
                ec2.authorizeSecurityGroupIngress(auth_request);

            System.out.println("Successfully added ingress policy to security group: " + group_name);
            
            /*Step-7: Authorizing new security group egress rules*/
            IpRange ip_range_out = new IpRange()
                    .withCidrIp("10.0.0.0/16");
            
            IpPermission ip_perm3 = new IpPermission()
                    .withIpProtocol("tcp")
                    .withToPort(22)
                    .withFromPort(22)
                    .withIpv4Ranges(ip_range_out);
            
            AuthorizeSecurityGroupEgressRequest auth_request2 = new
                    AuthorizeSecurityGroupEgressRequest()
                        .withGroupId(group_id)
                        .withIpPermissions(ip_perm3);

            AuthorizeSecurityGroupEgressResult auth_response2 =
                    ec2.authorizeSecurityGroupEgress(auth_request2);
            
            System.out.println("Successfully added egress policy to security group: " + group_name);
            
            
            /*Step-8: Describing the security group*/
            DescribeSecurityGroupsRequest request =
                    new DescribeSecurityGroupsRequest()
                        .withGroupIds(group_id);
            
            DescribeSecurityGroupsResult response =
                    ec2.describeSecurityGroups(request);
            
            for(SecurityGroup securityGroup : response.getSecurityGroups()) {
            	System.out.printf(
                        "Found Security Group with id %s, " +
                                "vpc id %s " +
                                "and ingress permissions %s" + "and egress permissions %s",
                        securityGroup.getGroupId(),
                        securityGroup.getVpcId(),
                        securityGroup.getIpPermissions().toString(),
                        securityGroup.getIpPermissionsEgress().toString());
            }
            
		
	}

}
