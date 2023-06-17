package com.yapp.bol.presentation.view.match.member_select

import KeyboardVisibilityUtils
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.yapp.bol.presentation.R
import com.yapp.bol.presentation.databinding.FragmentMemberSelectBinding
import com.yapp.bol.presentation.utils.GuestAddDialog
import com.yapp.bol.presentation.view.match.MatchViewModel
import com.yapp.bol.presentation.view.match.game_select.GameSelectFragment.Companion.GAME_NAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberSelectFragment : Fragment() {

    private var _binding: FragmentMemberSelectBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val matchViewModel: MatchViewModel by activityViewModels()
    private val memberSelectViewModel: MemberSelectViewModel by viewModels()

    private val memberSelectAdapter = MemberSelectAdapter { member ->
        memberSelectViewModel.checkedSelectMembers(member)
        memberSelectViewModel.clearMembers(member.id, getInputTextValue())
    }
    private val membersAdapter = MembersAdapter { member, isChecked ->
        memberSelectViewModel.checkedSelectMembers(member)
        memberSelectViewModel.updateMemberIsChecked(member.id, isChecked)
    }

    private val inputManager by lazy {
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val guestAddDialog by lazy {
        GuestAddDialog(
            context = requireContext(),
            addGuest = { name -> },
            getValidateNickName = { nickname -> memberSelectViewModel.getValidateNickName(10, nickname) },
        )
    }

    private val keyboardVisibilityUtils by lazy {
        KeyboardVisibilityUtils(
            window = activity?.window ?: throw Exception(),
            onHideKeyboard = { guestAddDialog.dismiss() },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMemberSelectBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val gameName = arguments?.getString(GAME_NAME) ?: ""
        matchViewModel.updateToolBarTitle(gameName)

        binding.rvMemberSelect.adapter = memberSelectAdapter
        binding.rvMembers.adapter = membersAdapter

        setViewModelObserve()
        setClickListener()

        binding.etSearchMember.doOnTextChanged { text, _, _, _ ->
            memberSelectViewModel.updateSearchMembers(text.toString())
        }

        binding.etSearchMember.onFocusChangeListener = setFocusChangeListener()

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                hideKeyboard()
                binding.etSearchMember.clearFocus()
            }
        }
        binding.rvMembers.addOnScrollListener(scrollListener)
        keyboardVisibilityUtils
    }

    private fun setViewModelObserve() {
        memberSelectViewModel.members.observe(viewLifecycleOwner) { members ->
            val isVisible = members.isEmpty()
            setSearchResultNothing(isVisible, getInputTextValue())
            membersAdapter.submitList(members)
        }

        memberSelectViewModel.isCompleteButtonEnabled.observe(viewLifecycleOwner) {
            binding.btnPlayerComplete.isEnabled = it
        }

        memberSelectViewModel.players.observe(viewLifecycleOwner) { players ->
            memberSelectAdapter.submitList(players)
        }

        memberSelectViewModel.isNickNameValidate.observe(viewLifecycleOwner) {
            if (guestAddDialog.isShowing) guestAddDialog.setNicknameValid(it)
        }
    }

    private fun setSearchResultNothing(isVisible: Boolean, keyword: String) {
        val visible = if (isVisible) View.VISIBLE else View.GONE
        val searchResult = String.format(resources.getString(R.string.search_result_nothing), keyword)
        binding.viewSearchResultNothing.visibility = visible
        binding.tvSearchResultNothingGuide.visibility = visible
        binding.btnGuestAddNothing.visibility = visible
        binding.tvSearchResultNothing.apply {
            text = searchResult
            visibility = visible
        }
    }

    private fun setClickListener() {
        binding.ivSearchIcon.setOnClickListener {
            if (binding.etSearchMember.isFocused) {
                binding.etSearchMember.text.clear()
                binding.etSearchMember.clearFocus()
            } else {
                showKeyboard(binding.etSearchMember)
                binding.etSearchMember.requestFocus()
            }
        }
        binding.btnTempMember.setOnClickListener {
            guestAddDialog.show()
        }
        binding.btnGuestAddNothing.setOnClickListener {
            hideKeyboard()
            guestAddDialog.show()
        }
    }

    private fun setFocusChangeListener(): View.OnFocusChangeListener {
        return View.OnFocusChangeListener { _, hasFocus ->
            val image = ContextCompat.getDrawable(
                requireContext(),
                if (hasFocus) R.drawable.ic_cancel_gray else R.drawable.ic_search,
            )
            binding.ivSearchIcon.setImageDrawable(image)
        }
    }

    private fun getInputTextValue(): String {
        return binding.etSearchMember.text.toString()
    }

    private fun hideKeyboard() {
        if (activity == null && activity?.currentFocus == null) return
        inputManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    private fun showKeyboard(editText: EditText) {
        if (activity == null && activity?.currentFocus == null) return
        inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        _binding = null
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroyView()
    }
}
