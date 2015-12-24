/*
 * �쐬��: 2005/01/03
 *
 */
package sglclient.pskey;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * X.509�t�@�C������舵���N���X
 * 
 * @author Hiroki
 * @version 1.0
 */
public class X509 {
	private KeyStore ks;
	
    /**
     * KeyStore�t�@�C����ǂݍ����X.509�ؖ�����������悤�ɂ��܂�
     * 
     * @param storepasswd KeyStore�t�@�C���̃p�X���[�h
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     */
    public X509(String filepath, String filename, String storepasswd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
    
            //KeyStore�t�@�C���̃��[�h
            this.ks = KeyStore.getInstance("JKS");
            File file = new File(filepath,filename);
            FileInputStream fis = new FileInputStream(file);
//          ks.load(fis,"projecttheta".toCharArray());
            ks.load(fis,storepasswd.toCharArray());
    }
    /**
     * �w�肳�ꂽalias�ɂ���ؖ�����ǂݍ��݂܂�
     * 
     * @param alias �G�C���A�X
     * @return alias�Ɋi�[����Ă���Certificate�I�u�W�F�N�g
     * @throws KeyStoreException �w�肳�ꂽalias�����݂��Ȃ��Ƃ��G���[���o���܂��B
     */
    public Certificate getCertificate(String alias) throws KeyStoreException{
    	return (this.ks.getCertificate(alias));
    }
    
    /**
     * �w�肳�ꂽalias�ɂ���RSA���J����Ԃ��܂�
     * 
     * @param alias �G�C���A�X
     * @return RSAPublicKey�I�u�W�F�N�g
     * @throws KeyStoreException alias�����݂��Ȃ��Ƃ��G���[���o���܂��B
     */
    public RSAPublicKey getRSAPublicKey(String alias) throws KeyStoreException{
    	//RSA���J���̎擾
    	return ((RSAPublicKey)this.ks.getCertificate(alias).getPublicKey());
    }
    
    /**
     * RSA�閧����Ԃ��܂��B
     * @param alias �G�C���A�X
     * @param keypasswd �閧���̃p�X���[�h
     * @return RSAPrivete�I�u�W�F�N�g
     * @throws KeyStoreException alias�����݂��Ȃ��Ƃ��G���[���o���܂��B
     * @throws NoSuchAlgorithmException 
     * @throws UnrecoverableKeyException �p�X���[�h���Ԉ���Ă���ꍇ�G���[���o���܂��B
     */
    public RSAPrivateKey getRSAPrivateKey(String alias, String keypasswd) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException{
    	//RSA�閧���̎擾
		return ((RSAPrivateKey)ks.getKey(alias, keypasswd.toCharArray()));
    }
    
    /**
     * �t�@�C�����p�X���[�h�ŕی삵�ĕۑ����܂��B
     * @param storepasswd ���̃p�X���[�h�ŕی삵�ĕۑ����܂��B
     * @throws IOException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public void saveKeyStore(String filepath, String filename, String storepasswd) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException{
   	   	//�t�@�C���ւ̏������ݏ���
       	FileOutputStream fos = new FileOutputStream(new File(filepath,filename));
		ks.store(fos,storepasswd.toCharArray());
		fos.close();
    }
    
	/**
	 * �w�肳�ꂽ�G�C���A�X��X.509�ؖ�����RSA�閧�����g����SHA1�ŏ�������byte�z���Ԃ��܂�
	 * 
	 * @param alias �G�C���A�X
	 * @param passwd �閧���̃p�X���[�h
	 * @return X.509�ؖ�����閧���ŏ�������byte�z��
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	public byte[] sign(String alias,String passwd) throws NoSuchAlgorithmException, SignatureException{
		//Signature�̏���
		Signature sig = Signature.getInstance( "SHA1withRSA" );
		try{
			//�閧���̎擾
			PrivateKey prikey = this.getRSAPrivateKey(alias,passwd);
			//�ؖ����̎擾
			Certificate cert1 = this.getCertificate(alias);
			
			//�����̐���
			sig.initSign(prikey);
			sig.update(cert1.getEncoded());
		}catch(Exception e){
			e.printStackTrace();
		}
		return (sig.sign());
		
	}
	
	/**
	 * �w�肳�ꂽ�G�C���A�X��X.509�ؖ����ɂ�����J��(RSA)�Ŏw�肳�ꂽ���������؂��܂��B
	 * 
	 * @param alias ���؂Ɏg���ؖ����̃G�C���A�X
	 * @param sign ���؂���byte�z��
	 * @return �����̐^�U
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 */
	public boolean verify(String alias,byte[] sign) throws NoSuchAlgorithmException, SignatureException{
		//Signature�̏���
		Signature sig = Signature.getInstance( "SHA1withRSA" );
		try{
			//���J���̎擾
			PublicKey pubkey = this.getRSAPublicKey(alias);
			//�ؖ����̎擾
			Certificate cert1 = this.getCertificate(alias);

			//�����̌���
			sig.initVerify(pubkey);
			sig.update(cert1.getEncoded());
		}catch(Exception e){
			e.printStackTrace();
		}		
		return(sig.verify(sign));
	}

}
