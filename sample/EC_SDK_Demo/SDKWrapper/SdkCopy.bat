@echo off

echo *************************************************************
echo ******************Copy OpenSDK Packet************************
echo *************************************************************
mkdir "temp"

echo *************************************************************
echo ******************Unzip IM packet***************************
echo *************************************************************

start winrar x ".\ImSDK\ImSDK.zip" "temp"


rem 延迟一会再进行后继操作，以保证前面的执行完成
echo Delay ....... 
ping -n 10 127.0.0.1>nul
echo ******************Unzip IM packet end***********************

echo *************************************************************
echo ******************Remove duplicate dependency ***************
echo *************************************************************

cd ".\temp"

::del ".\libs\armeabi\libanyofficesdk.so"
del ".\libs\armeabi\libcryptotsc.so"
del ".\libs\armeabi\libHME-Audio.so"
del ".\libs\armeabi\libipsi_crypto.so"
del ".\libs\armeabi\libipsi_osal.so"
del ".\libs\armeabi\libipsi_pse.so"
del ".\libs\armeabi\libipsi_ssl.so"
::"del ".\libs\armeabi\libjniapi.so"
del ".\libs\armeabi\libKMC.so"
::del ".\libs\armeabi\libLog4Android.so"
del ".\libs\armeabi\libsecurec.so"
del ".\libs\armeabi\libssltsc.so"
::del ".\libs\armeabi\libsvnapi.so"
::del ".\libs\armeabi\libtfcard.so"
del ".\libs\armeabi\libtscsvn.so"
::del ".\libs\armeabi\libtsm.so"
del ".\libs\armeabi\libtup_call_audio.so"
del ".\libs\armeabi\libtup_call_mediaservice.so"
del ".\libs\armeabi\libtup_cloudrecord.so"
del ".\libs\armeabi\libtup_cmpt_service.so"
del ".\libs\armeabi\libtup_commonlib.so"
del ".\libs\armeabi\libtup_confctrl.so"
del ".\libs\armeabi\libtup_ctd.so"
del ".\libs\armeabi\libtup_dns.so"
del ".\libs\armeabi\libtup_eaddr.so"
del ".\libs\armeabi\libtup_exception.so"
del ".\libs\armeabi\libtup_httpofflinefile.so"
del ".\libs\armeabi\libtup_https_clt.so"
del ".\libs\armeabi\libtup_httptrans.so"
del ".\libs\armeabi\libtup_login.so"
del ".\libs\armeabi\libtup_logone.so"
del ".\libs\armeabi\libtup_msg.so"
del ".\libs\armeabi\libtup_openmedia.so"
del ".\libs\armeabi\libtup_os_adapter.so"
del ".\libs\armeabi\libtup_publiclib.so"
del ".\libs\armeabi\libtup_rsa_encrypt.so"
del ".\libs\armeabi\libtup_so"cket.so"
del ".\libs\armeabi\libtup_so"cket_stg.so"
del ".\libs\armeabi\libtup_xml.so"
del ".\libs\armeabi\libtupService.so"
del ".\libs\armeabi\libuspsdk.so"
del ".\libs\armeabi\libwebsockets.so"
del ".\libs\armeabi\libtup_support.so"

del ".\open_src\android-support-v7-recyclerview.jar"
del ".\open_src\android-support-v13.jar"

del ".\platform\gson-2.3.1.jar"
del ".\platform\HME-Audio.jar"
del ".\platform\TupCmptService.jar"
del ".\platform\TupEaddr.jar"
del ".\platform\TupIm.jar"
del ".\platform\TupRsa.jar"
del ".\platform\TupService.jar"
del ".\platform\TupSocket.jar"

cd ..

xcopy /E /Y /I  ".\temp\*.*"            "..\ImSDK"
rmdir /s/q ".\temp"
rmdir /s/q ".\ImSDK"

echo .
echo .
echo *************************************************************
echo ******************Copy OpenSDK Packet Success****************
echo *************************************************************
echo . 
echo .
