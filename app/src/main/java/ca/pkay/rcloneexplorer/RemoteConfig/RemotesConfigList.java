package ca.pkay.rcloneexplorer.RemoteConfig;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

import ca.pkay.rcloneexplorer.R;
import ca.pkay.rcloneexplorer.databinding.ConfigListItemTemplateBinding;
import java9.util.stream.IntStream;

public class RemotesConfigList extends Fragment {

    public interface ProviderSelectedListener {
        void onProviderSelected(int provider);
    }

    public List<String> providers;
    private int[] selected = {-1};
    private RadioButton lastSelected;
    private ProviderSelectedListener listener;
    private Context context;

    public RemotesConfigList() {}

    public static RemotesConfigList newInstance() { return new RemotesConfigList(); }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        providers = Arrays.asList(getResources().getStringArray(R.array.provider_ids));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_list, container, false);
        setClickListeners(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProviderSelectedListener) {
            listener = (ProviderSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ProviderSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void setSelected(RadioButton radioButton, String provider) {
        if (lastSelected != null) {
            lastSelected.setChecked(false);
        }
        radioButton.setChecked(true);
        lastSelected = radioButton;
        selected[0] = providers.indexOf(provider);
    }

    private void setClickListeners(View view) {
        ViewGroup listContent = view.findViewById(R.id.config_content);

        view.findViewById(R.id.cancel).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
        view.findViewById(R.id.next).setOnClickListener(v -> listener.onProviderSelected(selected[0]));

        final String[] ids = getResources().getStringArray(R.array.provider_ids);
        String[] names = getResources().getStringArray(R.array.provider_names);
        String[] summaries = getResources().getStringArray(R.array.provider_summaries);
        TypedArray icons = getResources().obtainTypedArray(R.array.provider_icons);

        Integer[] sorted = IntStream.range(0, ids.length).boxed().toArray(Integer[]::new);
        Arrays.sort(sorted, (i, j) -> names[i].compareToIgnoreCase(names[j]));

        for (int i = 0, itemsLength = ids.length; i < itemsLength; i++) {
            int j = sorted[i]; // Construct list sorted by provider name, not order of declaration
            ConfigListItemTemplateBinding item = ConfigListItemTemplateBinding.inflate(getLayoutInflater());
            item.providerTv.setText(names[j]);
            item.providerSummary.setText(summaries[j]);
            item.providerIcon.setImageDrawable(icons.getDrawable(j));
            item.provider.setOnClickListener(v -> setSelected(item.providerRb, ids[j]));
            listContent.addView(item.getRoot());
        }
    }
}
