package mariusbinary.cursorr.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import mariusbinary.cursorr.R;

public class SliderItemFragment extends Fragment {

  private static final String ARG_POSITION = "slider-position";
  // prepare all title ids arrays

  @StringRes
  private static final int[] PAGE_TITLES =
          new int[] { R.string.welcome_1_title,
                  R.string.welcome_2_title,
                  R.string.welcome_3_title,
                  R.string.welcome_4_title };

  // prepare all subtitle ids arrays
  @StringRes
  private static final int[] PAGE_TEXT =
          new int[] {
                  R.string.welcome_1_summary,
                  R.string.welcome_2_summary,
                  R.string.welcome_3_summary,
                  R.string.welcome_4_summary
          };

  // prepare all subtitle images arrays
  @StringRes
  private static final int[] PAGE_IMAGE =
          new int[] {
                  R.drawable.logo_256px,
                  R.drawable.ic_slide_2,
                  R.drawable.ic_slide_3,
                  R.drawable.ic_slide_4
          };

  // prepare all background images arrays
  @StringRes
  private static final int[] BG_IMAGE = new int[] {
          R.drawable.help_background_1,
          R.drawable.help_background_2,
          R.drawable.help_background_3,
          R.drawable.help_background_4,
  };
  private int position;
  public SliderItemFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   *
   * @return A new instance of fragment SliderItemFragment.
   */
  public static mariusbinary.cursorr.fragments.SliderItemFragment newInstance(int position) {
    mariusbinary.cursorr.fragments.SliderItemFragment fragment = new mariusbinary.cursorr.fragments.SliderItemFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_POSITION, position);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      position = getArguments().getInt(ARG_POSITION);
    }
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_welcome, container, false);
  }
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // set page background
    view.setBackground(requireActivity().getDrawable(BG_IMAGE[position]));
    TextView title = view.findViewById(R.id.textView);
    TextView titleText = view.findViewById(R.id.textView2);
    ImageView imageView = view.findViewById(R.id.imageView);


    // Inflate the layout for this fragment
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
    if (position == 0) {
      params.width = dpToPx(250);
    } else {
      params.width = dpToPx(350);
    }
    imageView.setLayoutParams(params);

    // enable link
    titleText.setMovementMethod(LinkMovementMethod.getInstance());

    // set page title
    title.setText(PAGE_TITLES[position]);
    // set page sub title text
    titleText.setText(PAGE_TEXT[position]);
    // set page image
    imageView.setImageResource(PAGE_IMAGE[position]);
  }

  public int dpToPx(int dp) {
    float density = getActivity().getResources()
            .getDisplayMetrics()
            .density;
    return Math.round((float) dp * density);
  }
}