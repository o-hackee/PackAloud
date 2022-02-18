package ol.ko.packaloud

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ol.ko.packaloud.SpeechConfigStore.speechConfigPrefsDataStore
import ol.ko.packaloud.databinding.FragmentSettingsBinding

// TODO needs more work

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        val settingsRepo = SpeechConfigRepository(requireContext().speechConfigPrefsDataStore)
        lifecycleScope.launch {
            binding.editTextNumberDecimal.setText(settingsRepo.loadRate().first())

            binding.editTextNumberDecimal.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    lifecycleScope.launch {
                        settingsRepo.saveRate(s.toString())
                    }
                }

            })
        }

        binding.styleSpinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.speech_style_array, android.R.layout.simple_spinner_item)
        lifecycleScope.launch {
            val style = settingsRepo.loadStyle().first()
            binding.styleSpinner.setSelection(
                resources.getStringArray(R.array.speech_style_array).indexOf(if (style == "") "default" else style) // TODO
            )

            binding.styleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long
                ) {
                    val value = parent.getItemAtPosition(position) as String
                    lifecycleScope.launch {
                        settingsRepo.saveStyle(if (value == "default") "" else value)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
    }
}