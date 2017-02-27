package com.dr.vlad.memento.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dr.vlad.memento.FingerprintHandler;
import com.dr.vlad.memento.MainActivity;
import com.dr.vlad.memento.R;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by drinc on 1/29/2017.
 */

public class FingerprintAuthDialogFragment extends DialogFragment implements FingerprintHandler.Callback {

    public static final String KEY_NAME = "note_auth_key";
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;


    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
    private View mBackupContent;
    private EditText mPassword;
//    private ImageView icon;
//    private TextView mStatusTextView;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mPasswordDescriptionTextView;
    private Stage stage = Stage.FINGERPRINT;

    private MainActivity mActivity;
    private Long noteId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            noteId = bundle.getLong(getActivity().getResources().getString(R.string.key_note_id));
        } else {
            return;
        }


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getString(R.string.sign_in));
        View view = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) view.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mSecondDialogButton = (Button) view.findViewById(R.id.second_dialog_button);
        mSecondDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stage == Stage.FINGERPRINT) {
                    usePassword();
                } else {
                    verifyPassword();
                }
            }
        });

        mFingerprintContent = view.findViewById(R.id.fingerprint_container);
        mBackupContent = view.findViewById(R.id.backup_container);
        mPassword = (EditText) view.findViewById(R.id.password);
        mPasswordDescriptionTextView = (TextView) view.findViewById(R.id.password_description);
//        icon = (ImageView) view.findViewById(R.id.fingerprint_icon);
//        mStatusTextView = (TextView) view.findViewById(R.id.fingerprint_status);
        initAuth();
        updateStage();

        return view;
    }

    private void usePassword() {
        stage = Stage.PASSWORD;
        updateStage();

    }

    private void updateStage() {
        switch (stage) {
            case FINGERPRINT:
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setText(R.string.use_password);
                mFingerprintContent.setVisibility(View.VISIBLE);
                mBackupContent.setVisibility(View.GONE);
                break;

            case PASSWORD:
                mCancelButton.setText(R.string.cancel);
                mSecondDialogButton.setText(R.string.ok);
                mFingerprintContent.setVisibility(View.GONE);
                mBackupContent.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initAuth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initFingeprintAuth();
        } else {
            initPasswordAuth();
        }
    }

    private void initPasswordAuth() {
        stage = Stage.PASSWORD;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initFingeprintAuth() {
        stage = Stage.FINGERPRINT;

        keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {
            initPasswordAuth();
        }

        if (!keyguardManager.isKeyguardSecure()) {
            Toast.makeText(getActivity(), "Lockscreen security not eanbled in Settings", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Fingerprint authentication permision not enabled", Toast.LENGTH_LONG).show();
            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(getActivity(), "Register at least one fingerprint in Settings", Toast.LENGTH_SHORT).show();
            return;
        }

        generateKey();

        if (initCipher()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler handler = new FingerprintHandler(getActivity(), this);
            handler.startAuth(fingerprintManager, cryptoObject);
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT |
                    KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);

        }
    }

    private void verifyPassword() {
        if (!checkPassword(mPassword.getText().toString())) {
            Toast.makeText(getActivity(), "Password is not ok", Toast.LENGTH_SHORT).show();
            return;
        }
        stage = Stage.FINGERPRINT;
        mPassword.setText("");
        onAuthenticated();
    }

    /**
     * @return true if {@code password} is correct, false otherwise
     */
    private boolean checkPassword(String password) {
        // Assume the password is always correct.
        return password.length() > 0;
    }


    @Override
    public void onAuthenticated() {
        if (noteId != null) {
            mActivity.openNote(noteId);
        }
        dismiss();
    }

    @Override
    public void onError() {
        usePassword();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity) {
            mActivity = (MainActivity) context;
        }
    }

    public enum Stage {
        FINGERPRINT,
        PASSWORD
    }
}
